package queryBuilder

import queryBuilder.impl.HttpQueryBuilder
import queryBuilder.model.{EarthquakeQueryStructure, EarthquakeQueryStructureJsonProtocol}
import spray.json.*

trait QueryBuilder {
  def buildQuery(endpoint: String): String
}

object QueryBuilder extends EarthquakeQueryStructureJsonProtocol:


  def fromJson(json: String): QueryBuilder =
    JsonParser(json).convertTo[EarthquakeQueryStructure] match {
      case structure => createQueryBuilder(structure)
    }


  private def createQueryBuilder(structure: EarthquakeQueryStructure): QueryBuilder =
    structure.requestOptions.format.value match {
      case Some("geojson") => new HttpQueryBuilder(structure)
      case _ => throw new IllegalArgumentException("Unsupported format")
    }


