import org.apache.spark.sql.{SparkSession, DataFrame}
import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import java.io.{BufferedWriter, FileWriter}
import java.net.{URL, HttpURLConnection}
import java.io.{BufferedReader, InputStreamReader}

@main
def getDataEveryDay(): Unit = {
    val base_url = "https://earthquake.usgs.gov/fdsnws/event/1/query"
    val min_magnitude = 0
    val order_by = "time"
    val format_type = "geojson"

    var current_time = LocalDateTime.of(2023, 12, 1, 0, 0, 0)

    while (current_time.isBefore(LocalDateTime.of(2024, 1, 1, 0, 0, 0))) {
        // Calculate the end time by adding 15 days to the current time
        val end_time = current_time.plusDays(15)

        // Build the parameters for the GET request
        val params = Map(
            "format" -> format_type,
            "starttime" -> current_time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
            "endtime" -> end_time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
            "minmagnitude" -> min_magnitude.toString,
            "orderby" -> order_by
        )

        // Construct the full URL
        val full_url = s"$base_url?${params.map { case (k, v) => s"$k=$v" }.mkString("&")}"

        // Make an HTTP GET request
        val connection = new URL(full_url).openConnection().asInstanceOf[HttpURLConnection]
        connection.setRequestMethod("GET")

        // Read the response
        val reader = new BufferedReader(new InputStreamReader(connection.getInputStream))
        val current_json = Stream.continually(reader.readLine()).takeWhile(_ != null).mkString("\n")
        reader.close()

        // Append the new data to the existing file
        val outputFile = new BufferedWriter(new FileWriter("existing_data.json", true))
        outputFile.write(current_json)
        outputFile.close()

        println(s"Data collected for ${current_time.toLocalDate}")

        // Update the current_time for the next iteration by adding 15 days
        current_time = end_time
    }
    // Start Spark session
    val spark = SparkSession.builder
      .appName("EarthquakeDataCollector")
      .config("spark.master", "local")
      .config("spark.sql.debug.maxToStringFields", 1000)
      .getOrCreate()

    // Create a Spark DataFrame from the merged JSON file
    val merged_json: DataFrame = spark.read.json("existing_data.json")

    // Perform Spark operations on the DataFrame as needed

    // Save the merged JSON to a single JSON file using Spark
    val output_path = "new_response"
    merged_json.write.mode("overwrite").json(output_path)

    println(s"Data collection complete. Merged JSON saved to: $output_path")

    // Stop Spark session
    spark.stop()
}
