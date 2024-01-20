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

    var current_time = LocalDateTime.now()
    var yesterday = current_time.minusDays(1)

    val existingDataFile = "existing_data.json"
    val existingJson: Option[String] = try {
        Some(scala.io.Source.fromFile(existingDataFile).mkString)
    } catch {
        case _: java.io.FileNotFoundException => None
    }

    while (yesterday.isBefore(current_time)) {
        // Calculate the end time by adding 1 day to the start time
        val oneDayStep = yesterday.plusMonths(1)

        // Build the parameters for the GET request
        val params = Map(
            "format" -> format_type,
            "starttime" -> yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
            "endtime" -> oneDayStep.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
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

        // Append the new data to the existing content
        val updatedJson = existingJson.getOrElse("") + current_json

        // Convert the JSON strings to a Set to remove duplicates
        val uniqueJsonSet = updatedJson.split("\n").toSet

        // Write the unique data to the existing file
        val outputFile = new BufferedWriter(new FileWriter(existingDataFile))
        outputFile.write(uniqueJsonSet.mkString("\n"))
        outputFile.close()

        println(s"Data collected for ${yesterday.toLocalDate}")


        // Update the yesterday variable for the next iteration by adding 15 days
        yesterday = oneDayStep
    }
    // Start Spark session
    val spark = SparkSession.builder
      .appName("EarthquakeDataCollector")
      .config("spark.master", "local")
      .config("spark.sql.debug.maxToStringFields", 1000)
      .getOrCreate()

    // Create a Spark DataFrame from the merged JSON file
    val merged_json: DataFrame = spark.read.json("existing_data.json")

    // Save the JSON string to a single JSON file
//    val output_path = "C:\\Users\\marco\\OneDrive\\Desktop\\MSI_ALL\\MSI\\RuettelReport\\backend\\Analysis\\new_response.json"
//    val outputFile = new BufferedWriter(new FileWriter(output_path))
//    outputFile.write(merged_json)
//    outputFile.close()
    merged_json.write
      .format("mongodb")
      .mode("overwrite")
      .option("checkpointLocation", "/tmp/")
      .option("forceDeleteTempCheckpointLocation", "true")
      .option("spark.mongodb.connection.uri", "mongodb://media:media1234@127.0.0.1:27017/ruettelreport")
      .option("spark.mongodb.database", "ruettelreport")
      .option("spark.mongodb.collection", "dataModel") // tenant-name + realtime_analytics
      .save()

//    val output_path = "C:\\Users\\marco\\OneDrive\\Desktop\\MSI_ALL\\MSI\\RuettelReport\\backend\\Analysis\\new_response.json"
//    merged_json.write.mode("overwrite").json(output_path)
    //println(s"Data collection complete. Merged JSON saved to: $output_path")

    spark.stop()
}