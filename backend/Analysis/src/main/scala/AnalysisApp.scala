

import org.apache.spark.sql.SparkSession

/*
  For running this locally in IntelliJ:
    - check "Add dependencies with provided scope to classpath"
    - Add VM Option: "--add-exports java.base/sun.nio.ch=ALL-UNNAMED" (Fixes: class org.apache.spark.storage.StorageUtils$ (in unnamed module @0x4c163e3) cannot access class sun.nio.ch.DirectBuffer (in module java.base) because module java.base does not export sun.nio.ch to unnamed module @0x4c163e3)
 */
@main def run() =

  /*
  val spark = SparkSession.builder
    .appName("HelloWorld")
    .master(sys.env.getOrElse("SPARK_MASTER_URL", "local[*]"))
    .getOrCreate()           // 1
  import spark.implicits._   // 2

  val df = List("hello", "world").toDF  // 3
  df.show()                             // 4

  spark.stop

   */
  val logFile = "C:\\Program Files\\Spark\\spark-3.5.0-bin-hadoop3\\README.md" // Should be some file on your system
  val spark = SparkSession.builder.appName("Simple Application").config("spark.master", "local").getOrCreate()
  val logData = spark.read.textFile(logFile).cache()
  val numAs = logData.filter(line => line.contains("a")).count()
  val numBs = logData.filter(line => line.contains("b")).count()
  println(s"Lines with a: $numAs, Lines with b: $numBs")
  spark.stop()
