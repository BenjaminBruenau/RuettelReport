package queryBuilder

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

// Define case classes for the structure
case class FormatField(`type`: String, include: Boolean, value: String)
case class RequestOptions(format: FormatField,
                          endtime: Option[EndtimeField],
                          starttime: Option[StarttimeField],
                          minmagnitude: Option[MinMagnitudeField],
                          maxmagnitude: Option[MaxMagnitudeField],
                          minlongitude: Option[MinLongitudeField],
                          maxlongitude: Option[MaxLongitudeField],
                          minlatitude: Option[MinLatitudeField],
                          maxlatitude: Option[MaxLatitudeField])
case class EndtimeField(`type`: String, include: Boolean)
case class StarttimeField(`type`: String, include: Boolean)
case class MinMagnitudeField(`type`: String, include: Boolean, value: Double)
case class MaxMagnitudeField(`type`: String, include: Boolean, value: Double)
case class MinLongitudeField(`type`: String, include: Boolean, value: Double)
case class MaxLongitudeField(`type`: String, include: Boolean, value: Double)
case class MinLatitudeField(`type`: String, include: Boolean, value: Double)
case class MaxLatitudeField(`type`: String, include: Boolean, value: Double)

case class ApiEndpointConfig(url: String, method: String, params: Map[String, String])
case class EarthquakeQueryStructure(requestOptions: RequestOptions, api_endpoints: Map[String, ApiEndpointConfig])

// Define implicit JSON format for case classes using SprayJsonSupport and DefaultJsonProtocol
trait EarthquakeQueryStructureJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val formatFieldFormat: JsonFormat[FormatField] = jsonFormat3(FormatField.apply _)
  implicit val endtimeFieldFormat: JsonFormat[EndtimeField] = jsonFormat2(EndtimeField.apply _)
  implicit val starttimeFieldFormat: JsonFormat[StarttimeField] = jsonFormat2(StarttimeField.apply _)
  implicit val minMagnitudeFieldFormat: JsonFormat[MinMagnitudeField] = jsonFormat3(MinMagnitudeField.apply _)
  implicit val maxMagnitudeFieldFormat: JsonFormat[MaxMagnitudeField] = jsonFormat3(MaxMagnitudeField.apply _)
  implicit val minLongitudeFieldFormat: JsonFormat[MinLongitudeField] = jsonFormat3(MinLongitudeField.apply _)
  implicit val maxLongitudeFieldFormat: JsonFormat[MaxLongitudeField] = jsonFormat3(MaxLongitudeField.apply _)
  implicit val minLatitudeFieldFormat: JsonFormat[MinLatitudeField] = jsonFormat3(MinLatitudeField.apply _)
  implicit val maxLatitudeFieldFormat: JsonFormat[MaxLatitudeField] = jsonFormat3(MaxLatitudeField.apply _)
  implicit val requestOptionsFormat: JsonFormat[RequestOptions] = jsonFormat9(RequestOptions.apply _)
  implicit val apiEndpointConfigFormat: JsonFormat[ApiEndpointConfig] = jsonFormat3(ApiEndpointConfig.apply _)
}

// Implement the trait in your application
object EarthquakeQueryStructureJsonProtocol extends EarthquakeQueryStructureJsonProtocol
