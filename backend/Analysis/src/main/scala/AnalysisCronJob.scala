import commons.environment.EnvConfig.env
import org.apache.commons.math3.distribution.{BinomialDistribution, NormalDistribution}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.*
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}


@main def runAnalysisCronJob(): Unit =
  lazy val kafkaBootstrapServers: String = env("KAFKA_BOOTSTRAP_SERVERS", ???)
  lazy val mongoDBConnectionUri: String = env("MONGO_CONNECTION_URI", ???)

  val spark = SparkSession
    .builder
    .appName("KafkaSparkAnalysisCronJob")
    .master(sys.env.getOrElse("SPARK_MASTER_URL", "local[*]"))
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")
  import spark.implicits.*
  import scala3encoders.given


  val kafkaDF = spark.read
    .format("kafka")
    .option("kafka.bootstrap.servers", kafkaBootstrapServers)
    .option("subscribePattern", "features_.*")
    .option("startingOffsets", "earliest") // We want to aggregate all data of a tenant
    .load()
    .select(Symbol("timestamp").cast("string"), Symbol("key").cast("string"), Symbol("value").cast("string"))

  // As we are streaming from several topics we need to aggregate per tenant and per its users
  val enrichedDF = kafkaDF
    .withColumn("tenantId", split(col("key"), ":").getItem(0))
    .withColumn("userId", split(col("key"), ":").getItem(1))
    .select("tenantId", "userId", "timestamp", "value")

  val jsonSchema = schema_of_json(kafkaDF.select("value").first().getString(0))
  val jsonDF = enrichedDF
    .select(col("timestamp"),col("tenantId"), col("userId"),from_json(col("value"), jsonSchema).as("json"))
    .select("tenantId", "userId", "timestamp", "json.*")

  jsonDF.show()


  val tenantIds = jsonDF.select("tenantId").distinct().collect().map(_.getString(0))

  // Perform Analysis for each tenant
  tenantIds.foreach { tenantId =>

    val filteredDF = jsonDF.filter(jsonDF("tenantId") === tenantId)

    val collectionName = s"ruettel_report_$tenantId"

    val resultDF = performAnalysis(filteredDF, spark)

    resultDF.write
      .format("mongodb")
      .mode("append")
      .option("spark.mongodb.connection.uri", mongoDBConnectionUri)
      .option("spark.mongodb.database", "ruettelreport")
      .option("spark.mongodb.collection", collectionName)
      .option("spark.mongodb.convertJson", "any")
      .save()
  }


/**
 * This method will calculate three things for a given Dataframe:
 * 1. the time-range of the aggregated data (not of the features themselves but of the kafka messages -> aggregations for all of the queried data by the tenants)
 * 2. count and probability aggregation of each unique earthquake type (for each unique feature)
 * 3. time difference between all Events -> Mean and standard Deviation can be used for further analysis and predictions
 * @param jsonDF DataFrame to perform aggregations on
 * @param spark existing spark session for implicits
 * @return
 */
def performAnalysis(jsonDF: DataFrame, spark: SparkSession): DataFrame =
  import spark.implicits.*
  import scala3encoders.given

  val minMaxTimestamps = jsonDF
    .select(to_timestamp(col("timestamp")).as("timestamp"))
    .agg(
      min("timestamp").alias("minTimestamp"),
      max("timestamp").alias("maxTimestamp")
    )

  minMaxTimestamps.show()

  // Extract Json Data for easier Analysis
  val extractedData = jsonDF.select(
    col("properties.mag").alias("magnitude"),
    col("properties.dmin").alias("duration"),
    col("properties.sig").alias("significance"),
    col("properties.time").alias("event_time"),
    col("geometry.coordinates").alias("coordinates"),
    col("properties.type").alias("type")
  )

  // Explode the coordinates array to individual columns
  val explodedData = extractedData.withColumn("longitude", col("coordinates")(0))
    .withColumn("latitude", col("coordinates")(1))
    .withColumn("depth", col("coordinates")(2))
    .drop("coordinates")
    .dropDuplicates("event_time")

  val distributionData = analyseTimeDifferenceBetweenEvents(explodedData)

  val distributionDF = localSeqToDatasetHolder(distributionData).toDF().select(col("_1").alias("mean"), col("_2").alias("std"))

  distributionDF.show()

  val eventTypeProbabilities = analyseEventTypeProbabilities(explodedData)
  eventTypeProbabilities.show()


  /// Convert Computed Aggregations to JSON so they can be written to MongoDB easily

  val distributionJSONDF = pivotAndAggregateToJSON(distributionDF.withColumn("key", lit("distribution")), "key")
  distributionJSONDF.show(false)

  val timeRangeJSONDF = pivotAndAggregateToJSON(minMaxTimestamps.withColumn("key", lit("time_range")), "key")
  timeRangeJSONDF.show(false)

  val pivotedDF = pivotAndAggregateToJSON(eventTypeProbabilities, "type")
  pivotedDF.show(false)


  val eventTypeDataJSONDF = convertColumnsToSingleJSONColumn(pivotedDF, "eventTypeData")
  eventTypeDataJSONDF.show(false)

  /// Merge into one Dataframe

  // Add a unique index column to each DataFrame (to join on it later)
  val df1WithIndex = distributionJSONDF.withColumn("index", monotonically_increasing_id())
  val df2WithIndex = eventTypeDataJSONDF.withColumn("index", monotonically_increasing_id())
  val df3WithIndex = timeRangeJSONDF.withColumn("index", monotonically_increasing_id())

  val mergedDF = df1WithIndex.join(df2WithIndex, Seq("index"), "inner").join(df3WithIndex, Seq("index"), "inner").drop("index")

  mergedDF.show(false)



  // Examples on how to use the calculated Aggregations


  /// Predict probability of event happening in the next x seconds
  val loadedMean = distributionDF.select("mean").first().getDouble(0) / 1000
  val loadedStd = distributionDF.select("std").first().getDouble(0) / 1000
  println(s"Loaded mean: $loadedMean")
  println(s"Loaded std: $loadedStd")
  val newTimePoint = 1000
  val normalDistribution = new NormalDistribution(loadedMean, loadedStd)

  // Calculate the probability density function (PDF) at a specific point
  val pdfValue = normalDistribution.density(newTimePoint)
  println(s"Probability density at $newTimePoint: $pdfValue")

  // Calculate the cumulative distribution function (CDF) at a specific point
  val cdfValue = normalDistribution.cumulativeProbability(newTimePoint)
  println(s"Cumulative probability up to $newTimePoint: $cdfValue")

  val threshold = 0.0001

  if (cdfValue > threshold) {
    println(f"Probability of event happening in the next $newTimePoint seconds is $cdfValue%.4f %%")
  } else {
    println("The percentage is too low. Try a lower number of seconds.")
  }


  // Predict probability of next x events being of type eventType
  val eventType = "earthquake"
  val totalEvents = 100
  // Filter the DataFrame to get the probability for the specified event type
  val probability = eventTypeProbabilities
    .filter(col("type") === eventType)
    .select("probability")
    .as[Double]
    .first()

  // Example with a high totalEvents
  val totalEventsHigh = totalEvents
  val eventsToPredictAll = totalEventsHigh

  val binomialDistributionHigh = new BinomialDistribution(totalEventsHigh, probability)

  val probabilityAllEvents = binomialDistributionHigh.probability(eventsToPredictAll) * 100


  // Check if probability is above the threshold
  if (probabilityAllEvents > threshold) {
    println(f"The probability of all $totalEventsHigh events being earthquakes is: $probabilityAllEvents%.4f %%")
  } else {
    println("The percentage is too low. Try a lower number of events.")
  }
  mergedDF


/**
 * Calculates Time Difference between all Events
 * Mean and standard Deviation are calculated for further analysis
 * @param explodedData DataFrame with exploded Json data
 * @return mean and std
 */
def analyseTimeDifferenceBetweenEvents(explodedData: DataFrame): Seq[(Double, Double)] =
  val timeDiffs = explodedData.select("event_time").sort("event_time")
  val windowSpec = Window.orderBy("event_time")

  // Calculate the time differences using lag function
  val timeDiffsDF = explodedData
    .select(
      col("event_time"),
      lag("event_time", 1).over(windowSpec).alias("prev_event_time")
    )
    .withColumn("time_diff", when(col("prev_event_time").isNotNull, col("event_time") - col("prev_event_time")))
    .filter(col("prev_event_time").isNotNull)
    .drop("prev_event_time")

  val summaryDF = timeDiffsDF.describe("time_diff")

  val mean = summaryDF.select("time_diff").where("summary = 'mean'").first().getString(0).toDouble
  val std = summaryDF.select("time_diff").where("summary = 'stddev'").first().getString(0).toDouble

  // Create a DataFrame with mean and std values
  val distributionData = Seq((mean, std))
  distributionData



/**
 * Counts each unique event type and computes its probability
 * @param explodedData DataFrame with exploded Json data
* @return  DataFrame with count and probability per event type
 */
def analyseEventTypeProbabilities(explodedData: DataFrame): DataFrame =
  val typeData = explodedData.select("type").na.drop()

  val typeCounts = typeData.groupBy("type").count().sort(desc("count"))

  // probabilities of the different types
  val total = typeCounts.select("count").agg(sum("count")).first().getLong(0)
  val probabilities = typeCounts.withColumn("probability", col("count") / total)
  probabilities



/// SPARK JSON HELPER

/**
 * This method will pivot a dataframe by a pivotKey and convert all columns (except the pivot key) to json fields where their
 * values are the previous column values.
 * e.g.
 * +-----------------+------------------+------------------+
 * |              key|              mean|               std|
 * +-----------------+------------------+------------------+
 * |     distribution| 227860.9973753281|260151.34621610164|
 * +-----------------+------------------+------------------+
 *
 * will become
 *
 * +------------------------------------------------------+
 * |distribution                                          |
 * +------------------------------------------------------+
 * |{"mean": 227860.9973753281, "std": 260151.34621610164}|
 * +------------------------------------------------------+
 *
 * @param df Dataframe to pivot
 * @param pivotKey column key to pivot by
 */
def pivotAndAggregateToJSON(df: DataFrame, pivotKey: String): DataFrame =
  df
    .groupBy()
    .pivot(pivotKey) //first($"mean").alias("mean"), first($"std").alias("std")
    .agg(to_json(struct(df.drop(pivotKey).columns.map(columnName => first(columnName).alias(columnName)): _*)))

/**
 * Converts Columns of a DataFrame into a single json value into a new column
 * e.g.
 *
 * +--------------------+--------------------+--------------------+--------------------+--------------------+
 * |          earthquake|           explosion|           ice quake|    mining explosion|        quarry blast|
 * +--------------------+--------------------+--------------------+--------------------+--------------------+
 * |{"count":377,"pro...|{"count":2,"proba...|{"count":1,"proba...|{"count":1,"proba...|{"count":1,"proba...|
 * +--------------------+--------------------+--------------------+--------------------+--------------------+
 *
 * is converted to
 *
 * +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
 * |eventTypeData                                                                                                                                                                                                                                                                                                      |
 * +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
 * |{"earthquake":{"count":377,"probability":0.9869109947643979},"explosion":{"count":2,"probability":0.005235602094240838},"ice quake":{"count":1,"probability":0.002617801047120419},"mining explosion":{"count":1,"probability":0.002617801047120419},"quarry blast":{"count":1,"probability":0.002617801047120419}}|
 * +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
 * @param df
 * @param singleColumnName
 * @return
 */
def convertColumnsToSingleJSONColumn(df: DataFrame, singleColumnName: String): DataFrame =
  // Take first column value of first row to infer json schema
  val schema = schema_of_json(df.first().getString(0))
  df
    .withColumn(singleColumnName, to_json(struct(df.columns.map(columnName => from_json(col(columnName), schema).as(columnName)): _*)))
    .select(singleColumnName)