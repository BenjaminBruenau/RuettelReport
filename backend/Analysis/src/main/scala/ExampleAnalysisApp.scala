import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, count, from_json, schema_of_json}

/*
  On Windows:
    - download Spark https://spark.apache.org/downloads.html (3.5.0 + prebuilt for Apache Hadoop 3.3 and later)
    - extract somewhere
    - add HADOOP_HOME to environment variables (e.g. C:\Program Files\Spark\spark-3.5.0-bin-hadoop3)
    - add SPARK_HOME to environment variables (e.g. C:\Program Files\Spark\spark-3.5.0-bin-hadoop3)
    - add %SPARK_HOME%\bin and %HADOOP_HOME%\bin to path (make sure HADOOP_HOME is below SPARK_HOME)
    - add winutils.exe and hadoop.dll to %HADOOP_HOME%\bin from https://github.com/cdarlint/winutils/tree/master/hadoop-3.3.5/bin

  For running this locally in IntelliJ:
    - check "Add dependencies with provided scope to classpath"
    - Add VM Option: "--add-exports java.base/sun.nio.ch=ALL-UNNAMED" (Fixes: class org.apache.spark.storage.StorageUtils$ (in unnamed module @0x4c163e3) cannot access class sun.nio.ch.DirectBuffer (in module java.base) because module java.base does not export sun.nio.ch to unnamed module @0x4c163e3)
*/
@main def runApp() =
  val spark = SparkSession.builder.appName("KafkaTest").master("local[*]").getOrCreate()
  import spark.implicits._
  spark.sparkContext.setLogLevel("ERROR")


  /*
    For messages with values like this:

    {
                 "geometry": {
                     "coordinates": [
                         178.0089,
                         51.9332,
                         123.1
                     ],
                     "type": "Point"
                 },
                 "id": "ak023bmf0kuh",
                 "properties": {
                     "alert": null,
                     "cdi": null,
                     "code": "023bmf0kuh",
                     "detail": "https://earthquake.usgs.gov/fdsnws/event/1/query?eventid=ak023bmf0kuh&format=geojson",
                     "dmin": null,
                     "felt": null,
                     "gap": null,
                     "ids": ",ak023bmf0kuh,",
                     "mag": 2.3,
                     "magType": "ml",
                     "mmi": null,
                     "net": "ak",
                     "nst": null,
                     "place": "Rat Islands, Aleutian Islands, Alaska",
                     "rms": 0.82,
                     "sig": 81,
                     "sources": ",ak,",
                     "status": "reviewed",
                     "time": 1694304508259,
                     "title": "M 2.3 - Rat Islands, Aleutian Islands, Alaska",
                     "tsunami": 0,
                     "type": "earthquake",
                     "types": ",origin,phase-data,",
                     "tz": null,
                     "updated": 1695422986077,
                     "url": "https://earthquake.usgs.gov/earthquakes/eventpage/ak023bmf0kuh"
                 },
                 "type": "Feature"
             }

   */
  def batchOperations(ds: DataFrame, n: Long): Unit = {
    // Infer JSON schema from the first record in the batch
    val jsonSchema = schema_of_json(ds.select("value").first().getString(0))

    val jsonDF = ds.select(from_json(col("value"), jsonSchema).as("json"))
      .select("json.*")

    jsonDF.select("properties.*").show()

    val aggregationResult = jsonDF.groupBy("type").agg(count("*").as("count"))

    aggregationResult.show()

    // Write to some sink
    // ToDo: Advantage: WE could write to multiple sinks here (e.g. MongoDB and WebSocket) as we only want to aggregate batched data (one http request result to external api) anyway
    aggregationResult.write
      .format("console")
      .mode("append")
      .save()
  }

  val kafkaStream = spark.readStream.format("kafka")
    .option("kafka.bootstrap.servers", "localhost:29092")
    .option("subscribe", "feature_test")
    .option("startingOffsets", "earliest")
    .load()
    .select(Symbol("value").cast("string"))

  kafkaStream
    .writeStream
    .foreachBatch(batchOperations _)
    .start()
    .awaitTermination()

    /*
      ToDo:
      how about json daten (feature array fields aufsplitten) type, properties, id, geometry  als columns in kafka
      -> data frame so laden und dann direkt json transformationen drauf ausführen statt forEachBatch
     */