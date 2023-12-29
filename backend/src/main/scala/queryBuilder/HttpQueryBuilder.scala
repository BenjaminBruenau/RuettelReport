package queryBuilder

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Query

class HttpQueryBuilder(structure: EarthquakeQueryStructure) extends QueryBuilder {

  override def buildQuery(endpoint: String): String = {
    val endpointConfig = structure.api_endpoints.getOrElse(endpoint, throw new IllegalArgumentException(s"No config found for endpoint: $endpoint"))
    val url = endpointConfig.url
    val uri = Uri(url).withQuery(buildQueryParams(structure.requestOptions, endpointConfig.params))

    uri.toString()
  }

  private def buildQueryParams(requestOptions: RequestOptions, paramsMapping: Map[String, String]): Query = {
    // Extracting values from RequestOptions based on paramsMapping
    val queryParams = paramsMapping.map { case (_, key) =>
      key -> requestOptions.getClass.getDeclaredField(key).get(requestOptions).toString
    }

    Query(queryParams)
  }
}