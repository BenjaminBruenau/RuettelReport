import commons.environment.EnvConfig.env
import org.apache.commons.math3.distribution.{BinomialDistribution, NormalDistribution}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.*
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}


@main def runAnalysisCronJob(): Unit =
  lazy val kafkaBootstrapServers: String = env("KAFKA_BOOTSTRAP_SERVERS", ???)
  lazy val tenantId: String = env("TENANT_ID", ???)
  lazy val mongoDBConnectionUri: String = env("MONGO_CONNECTION_URI", ???)

  val spark = SparkSession
    .builder
    .appName("KafkaSparkAnalysisCronJob")
    .master(sys.env.getOrElse("SPARK_MASTER_URL", "local[*]"))
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")
  import scala3encoders.given
  import spark.implicits.*



  val kafkaDF = spark.read
    .format("kafka")
    .option("kafka.bootstrap.servers", kafkaBootstrapServers)
    .option("subscribe", tenantId)
    .option("startingOffsets", "earliest") // We want to aggregate all data of a tenant
    .load()
    .select(Symbol("timestamp").cast("string"), Symbol("key").cast("string"), Symbol("value").cast("string"))

  val jsonSchema = schema_of_json(kafkaDF.select("value").first().getString(0))
  println(jsonSchema)
  val jsonDF = kafkaDF
    .select(col("timestamp"),col("key"),from_json(col("value"), jsonSchema).as("json"))
    .select("timestamp", "key", "json.*")

  jsonDF.show()

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

  val pivoted = eventTypeProbabilities.groupBy()
    .pivot("type")
    .agg(first("count") as "count", first("probability") as "probability")
  pivoted.show()

  val pivotedDF = eventTypeProbabilities
    .groupBy()
    .pivot("type")
    .agg(to_json(struct(first($"count").alias("count"), first($"probability").alias("probability"))))

  pivotedDF.show()


  val newDF = pivotedDF.withColumn("eventTypeData", to_json(struct(pivotedDF.columns.map(columnName => col(columnName)):_*)))
  newDF.show()
  println(newDF.select("eventTypeData").first().getString(0))

  println("UHOHHH")
  //distributionDF.withColumn("eventTypeData", array(pivotedDF.co))
  val oneColumn = pivotedDF.select(concat(array(pivotedDF.columns.map(columnName => col(columnName)):_*)).as("eventTypeData"))
  oneColumn.show()
  //println(oneColumn.select("eventTypeData").first().getList(0))

  val updatedDf = pivotedDF
    .withColumn("earthquake", concat(lit("{\"type\": \"earthquake\", "), col("earthquake"), lit("}")))
    .withColumn("explosion", concat(lit("{\"type\": \"explosion\", "), col("explosion"), lit("}")))
    .withColumn("ice quake", concat(lit("{\"type\": \"ice quake\", "), col("ice quake"), lit("}")))
    .withColumn("mining explosion", concat(lit("{\"type\": \"mining explosion\", "), col("mining explosion"), lit("}")))
    .withColumn("quarry blast", concat(lit("{\"type\": \"quarry blast\", "), col("quarry blast"), lit("}")))
    .withColumn("json values", concat_ws(",", col("earthquake"), col("explosion"), col("ice quake"), col("mining explosion"), col("quarry blast")))
    .drop("earthquake", "explosion", "ice quake", "mining explosion", "quarry blast")

  updatedDf.show()

  println(updatedDf.select("json values").first().getString(0))

  pivotedDF.toJSON.show()
  println(pivotedDF.toJSON.select("value").first().getString(0))

  val eventTypeValueSchema = schema_of_json(pivotedDF.toJSON.select("value").first().getString(0).replace("\\", ""))
  val schema2 = schema_of_json(pivotedDF.toJSON.select("value").first().getString(0).replace("\"{", "{").replace("}\"", "}").replace("\\\"", "\""))
  println(schema_of_json(pivotedDF.toJSON.select("value").first().getString(0)))
  println(eventTypeValueSchema)
  println(schema2)
  val jsonPivot = pivotedDF.toJSON.select(from_json(col("value"), schema2).as("eventTypeData"))
  jsonPivot.show()


  val json = pivotedDF.toJSON.select(to_json(struct($"*")).as("eventTypeData"))
  json.show()
  println(json.toJSON.select("value").first().getString(0))

  newDF.write
    .format("mongodb")
    .mode("append")
    .option("spark.mongodb.connection.uri", mongoDBConnectionUri)
    .option("spark.mongodb.database", "ruettelreport")
    .option("spark.mongodb.collection", "test" + tenantId)
    .option("spark.mongodb.convertJson", "any")
    .save()



  // Example on how to use the calculated Aggregations


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