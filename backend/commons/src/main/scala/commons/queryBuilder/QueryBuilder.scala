package commons.queryBuilder

import commons.queryBuilder.QueryBuilderBackend.HTTP
import commons.queryBuilder.impl.HttpQueryBuilder
import commons.queryBuilder.model.{QueryStructure, QueryStructureJsonProtocol}

trait QueryBuilder:
  def buildQuery(structure: QueryStructure, endpoint: Option[String] = None): Option[String]

  def buildQuery(structure: QueryStructure, endpoint: String): Option[String] =
    buildQuery(structure, Some(endpoint))


object QueryBuilder extends QueryStructureJsonProtocol:
  
  def apply(backend: QueryBuilderBackend): QueryBuilder =
    backend match {
      case HTTP => HttpQueryBuilder()
      // e.g. case INFLUX => new InfluxQueryBuilder(structure)
      case _ => throw new IllegalArgumentException(s"Unsupported backend: $backend")
    }


enum QueryBuilderBackend:
  case HTTP