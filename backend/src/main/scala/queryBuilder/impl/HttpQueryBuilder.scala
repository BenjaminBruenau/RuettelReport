package queryBuilder.impl

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Query
import queryBuilder.QueryBuilder
import queryBuilder.model.{QueryStructure, RequestOptionField, RequestOptions}

class HttpQueryBuilder extends QueryBuilder {

  override def buildQuery(structure: QueryStructure, endpoint: Option[String]): Option[String] =
    endpoint.flatMap(endpoint => structure.api_endpoints.get(endpoint)).map { endpointConfig =>
      val url = endpointConfig.url
      val uri = Uri(url).withQuery(buildQueryParams(structure.requestOptions, endpointConfig.params))
      uri.toString()
    }
  
  private def buildQueryParams(requestOptions: RequestOptions, paramsMapping: Map[String, String]): Query =
    // ToDo: Error Handling for invalid QueryStructures

    val queryParams = paramsMapping.flatMap { case (queryParamName, requestOptionName) =>
      val field = requestOptions.getClass.getDeclaredField(requestOptionName)
      field.setAccessible(true) // Make the private field accessible
      val requestField = field.get(requestOptions).asInstanceOf[RequestOptionField[String | Double]]
      // Filter QueryParams without a value
      requestField.value.flatMap(v => if (requestField.include) Some(queryParamName -> v.toString) else None)
    }.toList.reverse.toMap

    Query(queryParams)

}
