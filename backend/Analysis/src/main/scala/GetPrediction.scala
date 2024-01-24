import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.spark.sql.SparkSession

@main
def getPrediction(): Unit =
  val spark = SparkSession.builder
    .appName("getPrediction")
    .config("spark.master", "local")
    .config("spark.sql.debug.maxToStringFields", 1000)
    .getOrCreate()

  // Load the DataFrame from the Parquet file
  val loadedDistributionDF = spark.read.parquet("C:\\Users\\marco\\OneDrive\\Desktop\\MSI_ALL\\MSI\\RuettelReport\\backend\\Analysis\\distribution.parquet")

  // Extract mean and std values from the loaded DataFrame
  val loadedMean = loadedDistributionDF.select("mean").first().getDouble(0) / 1000
  val loadedStd = loadedDistributionDF.select("std").first().getDouble(0) / 1000
  println(s"Loaded mean: $loadedMean")
  println(s"Loaded std: $loadedStd")

  val newTimePoint = 1000
  val normalDistribution = new NormalDistribution(loadedMean, loadedStd)

  // Example: Calculate the probability density function (PDF) at a specific point
  val pdfValue = normalDistribution.density(newTimePoint)

  println(s"Probability density at $newTimePoint: $pdfValue")

  // Example: Calculate the cumulative distribution function (CDF) at a specific point
  val cdfValue = normalDistribution.cumulativeProbability(newTimePoint)

  println(s"Cumulative probability up to $newTimePoint: $cdfValue")

  spark.stop()
