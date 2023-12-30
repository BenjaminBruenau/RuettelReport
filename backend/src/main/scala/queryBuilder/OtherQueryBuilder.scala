package queryBuilder

import spray.json._
import queryBuilder.{EarthquakeQueryStructure, RequestOptions, ApiEndpointConfig, EarthquakeQueryStructureJsonProtocol}

class OtherQueryBuilder extends QueryBuilder {
  private var requestOptions: RequestOptions = _
  private var apiEndpoints: Map[String, ApiEndpointConfig] = Map.empty

  //override def fromJson(json: String): QueryBuilder = {
  //  import EarthquakeQueryStructureJsonProtocol._
  //  val result = JsonParser(json).convertTo[EarthquakeQueryStructure]
  //  requestOptions = result.requestOptions
  //  apiEndpoints = result.api_endpoints
  //  this
  //}

  override def buildQuery(endpoint: String): String = {
    val endpointConfig = apiEndpoints.getOrElse(endpoint, throw new IllegalArgumentException(s"No config found for endpoint: $endpoint"))
    val url = endpointConfig.url
    val params = endpointConfig.params

    val queryParams = params.map { case (param, key) =>
      val value = requestOptions.getClass.getDeclaredField(key).get(requestOptions).toString
      s"$param=$value"
    }.mkString("&")

    s"$url?$queryParams"
  }
}
