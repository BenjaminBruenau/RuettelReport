package queryBuilder

import queryBuilder.impl.HttpQueryBuilder
import queryBuilder.model.{QueryStructure, QueryStructureJsonProtocol}
import spray.json.*

trait QueryBuilder {
  def buildQuery(endpoint: String): String
}

object QueryBuilder extends QueryStructureJsonProtocol:


  def fromJson(json: String): QueryBuilder =
    JsonParser(json).convertTo[QueryStructure] match {
      case structure => createQueryBuilder(structure)
    }


  private def createQueryBuilder(structure: QueryStructure): QueryBuilder =
    structure.requestOptions.format.value match {
      case Some("geojson") => new HttpQueryBuilder(structure)
      case _ => throw new IllegalArgumentException("Unsupported format")
    }


