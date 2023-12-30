package queryBuilder

import queryBuilder.EarthquakeQueryStructureJsonProtocol.{requestOptionsFormat, apiEndpointConfigFormat}
import spray.json._

trait QueryBuilder {
  def buildQuery(endpoint: String): String
}

object QueryBuilder extends DefaultJsonProtocol {

  // Define implicit JsonFormat for EarthquakeQueryStructure
  implicit val earthquakeQueryStructureFormat: JsonFormat[EarthquakeQueryStructure] = jsonFormat(
    EarthquakeQueryStructure.apply,
    "requestOptions", "api_endpoints"
  )

  def fromJson(json: String): QueryBuilder = {
    JsonParser(json).convertTo[EarthquakeQueryStructure] match {
      case structure => createQueryBuilder(structure)
    }
  }

  private def createQueryBuilder(structure: EarthquakeQueryStructure): QueryBuilder = {
    structure.requestOptions.format.value match {
      case "geojson" => new HttpQueryBuilder(structure)
      case _ => throw new IllegalArgumentException("Unsupported format")
    }
  }
}
