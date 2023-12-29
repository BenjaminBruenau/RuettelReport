import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OtherQueryBuilder extends QueryBuilder {
  private var requestOptions: RequestOptions = _
  private var apiEndpoints: Map[String, ApiEndpointConfig] = Map.empty

  override def fromJson(json: String): QueryBuilder = {
    val result = decode[EarthquakeQueryStructure](json)
    result match {
      case Right(queryStructure) =>
        requestOptions = queryStructure.requestOptions
        apiEndpoints = queryStructure.api_endpoints
        this
      case Left(error) =>
        throw new IllegalArgumentException(s"Error parsing JSON: $error")
    }
  }

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