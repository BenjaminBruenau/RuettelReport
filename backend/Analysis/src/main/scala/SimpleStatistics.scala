import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

@main
def simpleStatistics(): Unit = {
  val spark = SparkSession.builder
    .appName("EarthquakeDataProcessor")
    .config("spark.master", "local")
    .config("spark.sql.debug.maxToStringFields", 1000)
    .getOrCreate()

  // Read the data
  val data: DataFrame = spark.read.json("new_response")

  // Get the important data from the data frame
  val extractedData = data.select(
    format_number(col("properties.mag").cast("double"), 4).alias("magnitude"),
    format_number(col("properties.dmin").cast("double"), 4).alias("duration"),
    format_number(col("properties.sig").cast("double"), 4).alias("significance")
  )

  // Get the mean and std of Timediffs
  val timeDistributionDF = spark.read.parquet("C:\\Users\\marco\\OneDrive\\Desktop\\MSI_ALL\\MSI\\RuettelReport\\backend\\Analysis\\distribution.parquet")

  val loadedMean = timeDistributionDF.select("mean").first().getDouble(0) / 1000
  val loadedStd = timeDistributionDF.select("std").first().getDouble(0) / 1000

  // Get the data from the types of events and their count and possibilities
  val eventProbabilitiesDF = spark.read.parquet("C:\\Users\\marco\\OneDrive\\Desktop\\MSI_ALL\\MSI\\RuettelReport\\backend\\Analysis\\probabilities.parquet")

  // Format the probability column in eventProbabilitiesDF and multiply by 100
  val formattedEventProbabilitiesDF = eventProbabilitiesDF
    .withColumn("probability", expr("format_number(probability * 100, 4)"))

  formattedEventProbabilitiesDF.show()

  // Describe the data frame
  extractedData.na.drop().describe().show()


  spark.stop()

}
