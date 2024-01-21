import org.apache.commons.math3.distribution.BinomialDistribution
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.*


// TODO: For testing purposes only: Call the function with parameters like this: "getPredictionOfEventsOccuring("earthquake", 100)"
@main
def getPredictionOfEventsOccuring(eventType: String, totalEvents: Int): Unit =
  val spark = SparkSession.builder
    .appName("getPrediction")
    .config("spark.master", "local")
    .config("spark.sql.debug.maxToStringFields", 1000)
    .getOrCreate()

  import spark.implicits._

  // TODO: Load Data from MongoDB instead of Disk
  // Load the DataFrame from the Parquet file
  val loadedProbabilitiesDF = spark.read.parquet("C:\\Users\\marco\\OneDrive\\Desktop\\MSI_ALL\\MSI\\RuettelReport\\backend\\Analysis\\probabilities.parquet")

  // Filter the DataFrame to get the probability for the specified event type
  val probability = loadedProbabilitiesDF
    .filter(col("type") === "eventType")
    .select("probability")
    .as[Double]
    .first()

  // Example with a high totalEvents
  val totalEventsHigh = totalEvents
  val eventsToPredictAll = totalEventsHigh

  val binomialDistributionHigh = new BinomialDistribution(totalEventsHigh, probability)

  val probabilityAllEvents = binomialDistributionHigh.probability(eventsToPredictAll) * 100

  val threshold = 0.0001

  // Check if probability is above the threshold
  if (probabilityAllEvents > threshold) {
    println(f"The probability of all $totalEventsHigh events being earthquakes is: $probabilityAllEvents%.4f %%")
  } else {
    println("The percentage is too low. Try a lower number of events.")
  }

  spark.stop()



