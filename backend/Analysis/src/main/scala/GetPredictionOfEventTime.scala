import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.spark.sql.SparkSession

// Number of predTime should exceed like 2 Hours because a event is happening every 2 hours
// TODO: call it like this "getPredictionOfEventTime(1000)"

@main
def getPredictionOfEventTime(predTime: Int): Unit =
  val spark = SparkSession.builder
    .appName("getPrediction")
    .config("spark.master", "local")
    .config("spark.sql.debug.maxToStringFields", 1000)
    .getOrCreate()

  // Load the DataFrame from the Parquet file
  // TODO: Load Data from MongoDB instead of disk
  val loadedDistributionDF = spark.read.parquet("C:\\Users\\marco\\OneDrive\\Desktop\\MSI_ALL\\MSI\\RuettelReport\\backend\\Analysis\\distribution.parquet")

  // Extract mean and std values from the loaded DataFrame
  val loadedMean = loadedDistributionDF.select("mean").first().getDouble(0) / 1000
  val loadedStd = loadedDistributionDF.select("std").first().getDouble(0) / 1000
  println(s"Loaded mean: $loadedMean")
  println(s"Loaded std: $loadedStd")

  val normalDistribution = new NormalDistribution(loadedMean, loadedStd)

  // Calculate the cumulative distribution function (CDF) at a specific point
  val cdfValue = normalDistribution.cumulativeProbability(predTime) * 100

  val threshold = 0.0001

  if (cdfValue > threshold) {
    println(f"Probability of event happening in the next $predTime seconds is $cdfValue%.4f %%")
  } else {
    println("The percentage is too low. Try a lower number of seconds.")
  }

  spark.stop()
