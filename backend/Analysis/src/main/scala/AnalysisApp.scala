import org.apache.spark.sql.functions.*
import org.apache.spark.sql.types.{StringType, StructType}
import org.apache.spark.sql.{DataFrame, Encoder, Encoders, SparkSession}
import scala3encoders.given
import commons.environment.EnvConfig.env
/*
  On Windows:
    - download Spark https://spark.apache.org/downloads.html (3.5.0 + prebuilt for Apache Hadoop 3.3 and later)
    - extract somewhere
    - add %HADOOP_HOME% to environment variables (e.g. C:\Program Files\Spark\spark-3.5.0-bin-hadoop3)
    - add %SPARK_HOME% to environment variables (e.g. C:\Program Files\Spark\spark-3.5.0-bin-hadoop3)
    - add %SPARK_HOME%\bin and %HADOOP_HOME%\bin to path
    - add winutils.exe and hadoop.dll to %HADOOP_HOME%\bin from https://github.com/cdarlint/winutils/tree/master/hadoop-3.3.5/bin

  For running this locally in IntelliJ:
    - check "Add dependencies with provided scope to classpath"
    - Add VM Option: "--add-exports java.base/sun.nio.ch=ALL-UNNAMED" (Fixes: class org.apache.spark.storage.StorageUtils$ (in unnamed module @0x4c163e3) cannot access class sun.nio.ch.DirectBuffer (in module java.base) because module java.base does not export sun.nio.ch to unnamed module @0x4c163e3)
 */
@main def runAnalysisStream(): Unit =

  lazy val kafkaBootstrapServers: String = env("KAFKA_BOOTSTRAP_SERVERS", ???)
  lazy val mongoDBConnectionUri: String = env("MONGO_CONNECTION_URI", ???)

  val spark = SparkSession
    .builder
    .appName("KafkaSparkAnalysisStream")
    .master(sys.env.getOrElse("SPARK_MASTER_URL", "local[*]"))
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  //import spark.implicits._
  implicit val stringTupleEncoder: Encoder[(String, String)] = Encoders.tuple(Encoders.STRING, Encoders.STRING)


  val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", kafkaBootstrapServers)
    .option("failOnDataLoss", "false")
    .option("subscribePattern", "features_.*") // Stream from several topics
    .option("startingOffsets", "latest") //  latest (only new messages) or earliest - Read all messages (including older ones, not only new ones coming in), otherwise spark will start reading from the latest offset and thus only process new messages written to Kafka
    .load()


  val kafkaTopicDS = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)").as[(String, String)]

  // As we are streaming from several topics we need to aggregate per tenant and per its users
  val enrichedDF = kafkaTopicDS
    .withColumn("tenantId", split(col("key"), ":").getItem(0))
    .withColumn("userId", split(col("key"), ":").getItem(1))
    .select("tenantId", "userId", "value")



  val jsonValueDF = enrichedDF
    .select(col("tenantId"), col("userId"),from_json(col("value"), GeoJsonHelper.topLevelSchema).as("json"))
    .select("tenantId", "userId","json.*") // after here its possible to directly operate on each of the topLevel json fields and their values
    //.withColumn("timestamp", current_timestamp())
    //.withWatermark("timestamp", "1 minute")
    //.groupBy(window($"timestamp", "10 minutes", "5 minutes"), $"type").agg(count("*").as("count"))
  val watermark = "3 seconds"
  val windowDuration = "3 seconds"
  val slideDuration = "3 seconds"


  val aggregatedDF = jsonValueDF
    //.withColumn("timestamp", current_timestamp())
    //.withWatermark("timestamp", watermark)
    .groupBy(col("tenantId"), col("userId")) //window(col("timestamp"), windowDuration, slideDuration),
    .agg(
      count("*").as("count"),
      avg(when(col("properties.mag").isNotNull, col("properties.mag"))).as("avg_magnitude"),
      count(when(col("properties.mag") > 6.0, 1)).as("high_magnitude_count"),
    )


  val magDistribution = jsonValueDF
    .filter(col("properties.mag").isNotNull)
    .withColumn("mag_range",
      when(col("properties.mag").between(0, 2), "0-2")
        .when(col("properties.mag").between(2, 4), "2-4")
        .when(col("properties.mag").between(4, 6), "4-6")
        .when(col("properties.mag").between(6, 8), "6-8")
        .when(col("properties.mag").between(8, 10), "8-10")
        .otherwise("Unknown")
    )
    //.withColumn("timestamp", current_timestamp())
    //.withWatermark("timestamp", watermark)
    .groupBy(col("tenantId"), col("userId")) //window(col("timestamp"), windowDuration, slideDuration),
    // This is done to simulate pivot as it is not possible to use that in the context of streaming
    .agg(sum(when(col("mag_range") === "0-2", 1).otherwise(0)).alias("0-2"),
      sum(when(col("mag_range") === "2-4", 1).otherwise(0)).alias("2-4"),
      sum(when(col("mag_range") === "4-6", 1).otherwise(0)).alias("4-6"),
      sum(when(col("mag_range") === "6-8", 1).otherwise(0)).alias("6-8"),
      sum(when(col("mag_range") === "8-10", 1).otherwise(0)).alias("8-10"))


  // RUN AGGREGATIONS ON EACH QUERY (And write to MongoDB)
  val batchAggregations = jsonValueDF
    .writeStream
    .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
      batchOperations(batchDF, batchId, mongoDBConnectionUri)
    }
    .start()


  // OUTPUT IN MONGODB SINK AND/OR TERMINAL

  // COMPLETE MODE -> aggregate all data continuously, APPEND MODE -> AGGREGATE DATA PER BATCH (NEEDS WINDOW + WATERMARK)

  val mongoDBSink = aggregatedDF.writeStream
    .format("mongodb")
    .option("checkpointLocation", "./mongo-tmp/")
    .option("forceDeleteTempCheckpointLocation", "true")
    .option("spark.mongodb.connection.uri", mongoDBConnectionUri)
    .option("spark.mongodb.database", "ruettelreport")
    .option("spark.mongodb.collection", "analytics_complete")
    .outputMode("complete") // complete -> replace existing value in db (takes all values that came in into account, updates it each batch), append -> only new rows that were added since the last batch are written to sink, update -> writes only changed rows (new/updated) to the sink
    .start()

  val mongoDBMagSink = magDistribution.writeStream
    .format("mongodb")
    .option("checkpointLocation", "./mongo-tmp2/")
    .option("forceDeleteTempCheckpointLocation", "true")
    .option("spark.mongodb.connection.uri", mongoDBConnectionUri)
    .option("spark.mongodb.database", "ruettelreport")
    .option("spark.mongodb.collection", "analytics_complete_magdistr")
    .outputMode("complete")
    .start()


  val consoleSink = jsonValueDF.writeStream
    .outputMode("append")
    //.foreachBatch(dfOps _)
    .format("console")
    .start()


  val consoleSink1 = magDistribution.writeStream
    .outputMode("complete")
    //.foreachBatch(dfOps _)
    .format("console")
    .start()

  val consoleSink2 = aggregatedDF.writeStream
    .outputMode("complete")
    //.foreachBatch(dfOps _)
    .format("console")
    .start()

  mongoDBSink.awaitTermination()
  mongoDBMagSink.awaitTermination()
  consoleSink1.awaitTermination()
  consoleSink2.awaitTermination()
  consoleSink.awaitTermination()


  batchAggregations.awaitTermination()


/*
  This will be run foreach incoming Message Batch to perform aggregations on the results of one Query

  // It would also be possible here to infer JSON schema from the first record in the batch (this could lead to errors on the way though)
  val jsonSchema = schema_of_json(ds.select("value").first().getString(0))

  val jsonDF = ds.select(from_json(col("value"), jsonSchema).as("json"))
    .select("json.*")

  jsonDF.select("properties.*").show()

  val aggregationResult = jsonDF.groupBy("type").agg(count("*").as("count"))

  aggregationResult.show()
 */
def batchOperations(jsonValueDF: DataFrame, n: Long, mongoDBConnectionUri: String): Unit =

  val aggregatedDF = jsonValueDF
    .groupBy(col("tenantId"), col("userId"))
    .agg(
      count("*").as("count"),
      avg(when(col("properties.mag").isNotNull, col("properties.mag"))).as("avg_magnitude"),
      count(when(col("properties.mag") > 6.0, 1)).as("high_magnitude_count"),
    )


  val magDistribution = jsonValueDF
    .filter(col("properties.mag").isNotNull)
    .withColumn("mag_range",
      when(col("properties.mag").between(0, 2), "0-2")
        .when(col("properties.mag").between(2, 4), "2-4")
        .when(col("properties.mag").between(4, 6), "4-6")
        .when(col("properties.mag").between(6, 8), "6-8")
        .when(col("properties.mag").between(8, 10), "8-10")
        .otherwise("Unknown")
    )
    .groupBy(col("tenantId"), col("userId"))
    // This is done to simulate pivot as it is not possible to use that in the context of streaming
    .agg(sum(when(col("mag_range") === "0-2", 1).otherwise(0)).alias("0-2"),
      sum(when(col("mag_range") === "2-4", 1).otherwise(0)).alias("2-4"),
      sum(when(col("mag_range") === "4-6", 1).otherwise(0)).alias("4-6"),
      sum(when(col("mag_range") === "6-8", 1).otherwise(0)).alias("6-8"),
      sum(when(col("mag_range") === "8-10", 1).otherwise(0)).alias("8-10"))

  aggregatedDF.write
    .format("mongodb")
    .mode("append")
    .option("spark.mongodb.connection.uri", mongoDBConnectionUri)
    .option("spark.mongodb.database", "ruettelreport")
    .option("spark.mongodb.collection", "analytics")
    .save()

  magDistribution.write
    .format("mongodb")
    .mode("append")
    .option("spark.mongodb.connection.uri", mongoDBConnectionUri)
    .option("spark.mongodb.database", "ruettelreport")
    .option("spark.mongodb.collection", "analytics_magdistr")
    .save()
