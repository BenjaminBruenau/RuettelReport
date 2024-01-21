import org.apache.spark.sql.functions.*
import org.apache.spark.sql.types.{StringType, StructType}
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}

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

  implicit val stringTupleEncoder: Encoder[(String, String)] = Encoders.tuple(Encoders.STRING, Encoders.STRING)


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
    .add("properties", StringType)


  val kafkaTopicDS = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)").as[(String, String)]

  val jsonValueDF = kafkaTopicDS
    .select(from_json(col("value"), topLevelSchema).as("json"))
    .select("json.*") // after here its possible to directly operate on each of the topLevel fields and their values
    .groupBy("type").agg(count("*").as("count"))
    //.withColumn("timestamp", current_timestamp())
    //.withWatermark("timestamp", "10 minutes")
    //.groupBy(window($"timestamp", "10 minutes", "5 minutes"), $"type").agg(count("*").as("count"))

  /*
  val query = jsonValueDF.writeStream
    .outputMode("complete")
    //.foreachBatch(dfOps _)
    .format("console")
    .start()
  query.awaitTermination()

   */


  val mongoDBSink = jsonValueDF.writeStream
    .format("mongodb")
    .option("checkpointLocation", "/tmp/")
    .option("forceDeleteTempCheckpointLocation", "true")
    .option("spark.mongodb.connection.uri", "mongodb://127.0.0.1:27017/ruettelreport")
    .option("spark.mongodb.database", "ruettelreport")
    .option("spark.mongodb.collection","analytics") // tenant-name + realtime_analytics
    .outputMode("complete") //ToDo: choose appropriate output mode for our use case, complete -> replace existing value in db
    .start()

  mongoDBSink.awaitTermination()



def testSpark(): Unit =

  val logFile = "C:\\Program Files\\Spark\\spark-3.5.0-bin-hadoop3\\README.md" // Should be some file on your system
  val spark = SparkSession.builder.appName("Simple Application").config("spark.master", "local").getOrCreate()
  val logData = spark.read.textFile(logFile).cache()
  val numAs = logData.filter(line => line.contains("a")).count()
  val numBs = logData.filter(line => line.contains("b")).count()
  println(s"Lines with a: $numAs, Lines with b: $numBs")
  spark.stop()
