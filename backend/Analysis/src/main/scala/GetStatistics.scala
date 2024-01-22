import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions.*
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}

@main
def statistics(): Unit =
  val spark = SparkSession.builder
    .appName("EarthquakeDataProcessor")
    .config("spark.master", "local")
    .config("spark.sql.debug.maxToStringFields", 1000)
    .getOrCreate()

  // Read the data from the new_response.json file into a DataFrame
  val merged_json: DataFrame = spark.read.json("new_response")
  // Extract necessary fields
  val extractedData = merged_json.select(
    col("properties.mag").alias("magnitude"),
    col("properties.dmin").alias("duration"),
    col("properties.sig").alias("significance"),
    col("properties.time").alias("event_time"),
    col("geometry.coordinates").alias("coordinates"),
    col("properties.type").alias("type")
  )

  // Explode the coordinates array to individual columns
  var explodedData = extractedData.withColumn("longitude", col("coordinates")(0))
    .withColumn("latitude", col("coordinates")(1))
    .withColumn("depth", col("coordinates")(2))
    .drop("coordinates")

  // Show the extracted and exploded DataFrame
  explodedData = explodedData.drop()
  explodedData = explodedData.dropDuplicates("event_time")
  explodedData.show()

  // Perform Spark operations on the DataFrame, e.g., describe
  explodedData.describe().show()

  /*
  // Calculate the time differences between events
   */
  val timeDiffs = explodedData.select("event_time").sort("event_time")

  timeDiffs.show()

  // Assuming you have a DataFrame named explodedData with a column "event_time"
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

  timeDiffsDF.show()

  val summaryDF = timeDiffsDF.describe("time_diff")

  val mean = summaryDF.select("time_diff").where("summary = 'mean'").first().getString(0).toDouble
  val std = summaryDF.select("time_diff").where("summary = 'stddev'").first().getString(0).toDouble

  // TODO: Write to MongoDB

  // Create a DataFrame with mean and std values
  val distributionData = Seq((mean, std))
  val schema = StructType(Seq(
    StructField("mean", DoubleType, nullable = false),
    StructField("std", DoubleType, nullable = false)
  ))
  val distributionDF = spark.createDataFrame(spark.sparkContext.parallelize(distributionData).map(Row.fromTuple), schema)

  // Save the DataFrame to a file (in Parquet format)
  distributionDF.write.mode("overwrite").parquet("C:\\Users\\marco\\OneDrive\\Desktop\\MSI_ALL\\MSI\\RuettelReport\\backend\\Analysis\\distribution.parquet")
  // TODO: Get probabilities of different types and write them to a JSON file and write it to MongoDB

  var typeData = explodedData.select("type")
  typeData = typeData.na.drop()
  typeData.show()

  val typeCounts = typeData.groupBy("type").count().sort(desc("count"))
  typeCounts.show()

  // Now give me the possibilities of the different types
  val total = typeCounts.select("count").agg(sum("count")).first().getLong(0)
  val probabilities = typeCounts.withColumn("probability", col("count") / total)
  probabilities.show()

  // TODO: Write to MongoDB
  // Save the DataFrame into a json file
  probabilities.write.mode("overwrite").parquet("C:\\Users\\marco\\OneDrive\\Desktop\\MSI_ALL\\MSI\\RuettelReport\\backend\\Analysis\\probabilities.parquet")

  spark.stop()
