import org.apache.spark.sql.functions.*
import org.apache.spark.sql.types.{StringType, StructType}
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import scala3encoders.given
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
@main def run() =

  val spark = SparkSession
    .builder
    .appName("KafkaTest")
    .master(sys.env.getOrElse("SPARK_MASTER_URL", "local[*]"))
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._
  implicit val stringTupleEncoder: Encoder[(String, String)] = Encoders.tuple(Encoders.STRING, Encoders.STRING)


  val propertiesSchema = new StructType()
    .add("alert", StringType)
    .add("cdi", StringType)
    .add("code", StringType)
    .add("detail", StringType)
    .add("dmin", StringType)
    .add("felt", StringType)
    .add("gap", StringType)
    .add("ids", StringType)
    .add("mag", StringType)
    .add("magType", StringType)
    .add("mmi", StringType)
    .add("net", StringType)
    .add("nst", StringType)
    .add("place", StringType)
    .add("rms", StringType)
    .add("sig", StringType)
    .add("sources", StringType)
    .add("status", StringType)
    .add("time", StringType)
    .add("title", StringType)
    .add("tsunami", StringType)
    .add("type", StringType)
    .add("types", StringType)
    .add("tz", StringType)
    .add("updated", StringType)
    .add("url", StringType)


  val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:29092")
    .option("subscribe", "feature_test")
    .option("startingOffsets", "earliest") // Read all messages (including older ones, not only new ones coming in), otherwise spark will start reading from the latest offset and thus only process new messages written to Kafka
    .load()


  val topLevelSchema = new StructType()
    .add("id", StringType)
    .add("geometry", StringType)
    .add("type", StringType)
    .add("properties", propertiesSchema)


  val kafkaTopicDS = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)").as[(String, String)]

  val jsonValueDF = kafkaTopicDS
    .select(from_json(col("value"), topLevelSchema).as("json"))
    .select("json.*") // after here its possible to directly operate on each of the topLevel fields and their values
    //.withColumn("timestamp", current_timestamp())
    //.withWatermark("timestamp", "1 minute")
    //.groupBy(window($"timestamp", "10 minutes", "5 minutes"), $"type").agg(count("*").as("count"))

  val windowDuration = "20 seconds"
  val slideDuration = "20 seconds"



  val aggregatedDF = jsonValueDF
    .withColumn("timestamp", current_timestamp())
    .withWatermark("timestamp", "1 minute")
    .groupBy(window(col("timestamp"), windowDuration, slideDuration), col("type"))
    .agg(
      count("*").as("count"),
      avg(when(col("properties.mag").isNotNull, col("properties.mag"))).as("avg_magnitude"),
      count(when(col("properties.mag") > 6.0, 1)).as("high_magnitude_count"),
    )
    //.join(earthquakeFrequencyByLocation, Seq("properties.place"), "left_outer")

  // OUTPUT IN TERMINAL OR MONGODB SINK

  /*
  val query = aggregatedDF.writeStream
    .outputMode("complete")
    //.foreachBatch(dfOps _)
    .format("console")
    .start()

  query.awaitTermination()

   */
  val magRanges = Seq("0-2", "2-4", "4-6", "6-8", "8-10")
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
    .groupBy("mag_range")
    .agg(count("*").as("count"))


  val stddevMagnitudeByType = jsonValueDF
    .filter(col("properties.mag").isNotNull)
    .groupBy(col("type"))
    .agg(stddev("properties.mag").as("stddev_magnitude"))


  val mongoDBSink = magDistribution.writeStream
    .format("mongodb")
    .option("checkpointLocation", "./tmp/")
    .option("forceDeleteTempCheckpointLocation", "true")
    .option("spark.mongodb.connection.uri", "mongodb://127.0.0.1:27017/ruettelreport")
    .option("spark.mongodb.database", "ruettelreport")
    .option("spark.mongodb.collection","analyticsmagdistro") // tenant-name + realtime_analytics
    .outputMode("complete") // complete -> replace existing value in db (takes all values that came in into account, updates it each batch), append -> only new rows that were added since the last batch are written to sink, update -> writes only changed rows (new/updated) to the sink
    .start()

  val query = magDistribution.writeStream
    .outputMode("complete")
    //.foreachBatch(dfOps _)
    .format("console")
    .start()

  mongoDBSink.awaitTermination()
  query.awaitTermination()



def testSpark(): Unit =

  val logFile = "C:\\Program Files\\Spark\\spark-3.5.0-bin-hadoop3\\README.md" // Should be some file on your system
  val spark = SparkSession.builder.appName("Simple Application").config("spark.master", "local").getOrCreate()
  val logData = spark.read.textFile(logFile).cache()
  val numAs = logData.filter(line => line.contains("a")).count()
  val numBs = logData.filter(line => line.contains("b")).count()
  println(s"Lines with a: $numAs, Lines with b: $numBs")
  spark.stop()
