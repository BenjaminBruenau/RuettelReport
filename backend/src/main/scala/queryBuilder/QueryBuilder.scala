import io.circe.Decoder
import io.circe.parser._

trait QueryBuilder {
  def buildQuery(endpoint: String): String
}

object QueryBuilder {

  // Define implicit decoder for EarthquakeQueryStructure
  implicit val earthquakeQueryStructureDecoder: Decoder[EarthquakeQueryStructure] = deriveDecoder

  def fromJson(json: String): QueryBuilder = {
    // Implement JSON parsing and case class creation here using circe
    decode[EarthquakeQueryStructure](json).fold(
      error => throw new IllegalArgumentException(s"Error parsing JSON: $error"),
      structure => createQueryBuilder(structure)
    )
  }

  private def createQueryBuilder(structure: EarthquakeQueryStructure): QueryBuilder = {
    structure.requestOptions.format.value match {
      case "geojson" => new HttpQueryBuilder(structure)
      case _ => throw new IllegalArgumentException("Unsupported format")
    }
  }
}