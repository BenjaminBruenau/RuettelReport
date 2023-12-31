package queryBuilder.impl

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Query
import queryBuilder.QueryBuilder
import queryBuilder.model.{EarthquakeQueryStructure, RequestOptionField, RequestOptions}

class HttpQueryBuilder(structure: EarthquakeQueryStructure) extends QueryBuilder {

  override def buildQuery(endpoint: String): String =
    val endpointConfig = structure.api_endpoints.getOrElse(endpoint, throw new IllegalArgumentException(s"No config found for endpoint: $endpoint"))
    val url = endpointConfig.url
    val uri = Uri(url).withQuery(buildQueryParams(structure.requestOptions, endpointConfig.params))

    uri.toString()


  private def buildQueryParams(requestOptions: RequestOptions, paramsMapping: Map[String, String]): Query =
    // ToDo: Error Handling for invalid QueryStructures

    val queryParams = paramsMapping.map { case (queryParamName, requestOptionName) =>
      val field = requestOptions.getClass.getDeclaredField(requestOptionName)
      field.setAccessible(true) // Make the private field accessible
      val value = field.get(requestOptions).asInstanceOf[RequestOptionField[String | Double]].value

      // Filter QueryParams without a value
      value.map(v => queryParamName -> value.get.toString)
    }.flatten

    Query(queryParams.toMap)

}
