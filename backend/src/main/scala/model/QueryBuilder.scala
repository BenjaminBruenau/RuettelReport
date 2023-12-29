import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class QueryBuilder {

  private var requestOptions: Map[String, Any] = Map.empty
  private var apiEndpoints: Map[String, Map[String, Any]] = Map.empty

  def setStartTime(startTime: LocalDateTime): QueryBuilder = {
    requestOptions += ("starttime" -> startTime.format(DateTimeFormatter.ISO_DATE_TIME))
    this
  }

  def setEndTime(endTime: LocalDateTime): QueryBuilder = {
    requestOptions += ("endtime" -> endTime.format(DateTimeFormatter.ISO_DATE_TIME))
    this
  }

  def setMinMagnitude(minMagnitude: Double): QueryBuilder = {
    requestOptions += ("minmagnitude" -> minMagnitude)
    this
  }

  def setMaxMagnitude(maxMagnitude: Double): QueryBuilder = {
    requestOptions += ("maxmagnitude" -> maxMagnitude)
    this
  }

  def setMinLongitude(minLongitude: Double): QueryBuilder = {
    requestOptions += ("minlongitude" -> minLongitude)
    this
  }

  def setMaxLongitude(maxLongitude: Double): QueryBuilder = {
    requestOptions += ("maxlongitude" -> maxLongitude)
    this
  }

  def setMinLatitude(minLatitude: Double): QueryBuilder = {
    requestOptions += ("minlatitude" -> minLatitude)
    this
  }

  def setMaxLatitude(maxLatitude: Double): QueryBuilder = {
    requestOptions += ("maxlatitude" -> maxLatitude)
    this
  }

  def buildQuery(endpoint: String): String = {
    val endpointConfig = apiEndpoints.getOrElse(endpoint, Map.empty)
    val url = endpointConfig.getOrElse("url", "").toString
    val params = endpointConfig.getOrElse("params", Map.empty).asInstanceOf[Map[String, String]]

    val queryParams = params.map { case (param, key) =>
      val value = requestOptions.getOrElse(key, "").toString
      s"$param=$value"
    }.mkString("&")

    s"$url?$queryParams"
  }
}

object QueryBuilder {
  def apply(apiEndpoints: Map[String, Map[String, Any]]): QueryBuilder = {
    val builder = new QueryBuilder()
    builder.apiEndpoints = apiEndpoints
    builder
  }
}