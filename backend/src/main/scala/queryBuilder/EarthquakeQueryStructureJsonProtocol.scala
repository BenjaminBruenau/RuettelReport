package queryBuilder

import io.circe.{Decoder, HCursor}
import io.circe.generic.semiauto.deriveDecoder

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

// Define implicit decoder for EarthquakeQueryStructure
object EarthquakeQueryStructureJsonProtocol {
  implicit val formatFieldDecoder: Decoder[FormatField] = deriveDecoder
  implicit val endtimeFieldDecoder: Decoder[EndtimeField] = deriveDecoder
  implicit val starttimeFieldDecoder: Decoder[StarttimeField] = deriveDecoder
  implicit val minMagnitudeFieldDecoder: Decoder[MinMagnitudeField] = deriveDecoder
  implicit val maxMagnitudeFieldDecoder: Decoder[MaxMagnitudeField] = deriveDecoder
  implicit val minLongitudeFieldDecoder: Decoder[MinLongitudeField] = deriveDecoder
  implicit val maxLongitudeFieldDecoder: Decoder[MaxLongitudeField] = deriveDecoder
  implicit val minLatitudeFieldDecoder: Decoder[MinLatitudeField] = deriveDecoder
  implicit val maxLatitudeFieldDecoder: Decoder[MaxLatitudeField] = deriveDecoder
  implicit val requestOptionsDecoder: Decoder[RequestOptions] = deriveDecoder
  implicit val apiEndpointConfigDecoder: Decoder[ApiEndpointConfig] = deriveDecoder
  implicit val earthquakeQueryStructureDecoder: Decoder[EarthquakeQueryStructure] = deriveDecoder
}
