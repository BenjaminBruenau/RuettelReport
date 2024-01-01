package queryBuilder.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.*


case class RequestOptions(format: RequestOptionField[String], endtime: RequestOptionField[String], starttime: RequestOptionField[String], minmagnitude: RequestOptionField[Double], maxmagnitude: RequestOptionField[Double], minlongitude: RequestOptionField[Double], maxlongitude: RequestOptionField[Double], minlatitude: RequestOptionField[Double], maxlatitude: RequestOptionField[Double])

case class RequestOptionField[T](`type`: String, include: Boolean, value: Option[T])


case class ApiEndpointConfig(url: String, method: String, params: Map[String, String])
case class EarthquakeQueryStructure(requestOptions: RequestOptions, api_endpoints: Map[String, ApiEndpointConfig])

trait EarthquakeQueryStructureJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol:
  implicit val requestOptionFieldStringValue: JsonFormat[RequestOptionField[String]] = jsonFormat3(RequestOptionField[String].apply)
  implicit val requestOptionFieldDoubleValue: JsonFormat[RequestOptionField[Double]] = jsonFormat3(RequestOptionField[Double].apply)
  implicit val requestOptionsFormat: JsonFormat[RequestOptions] = jsonFormat9(RequestOptions.apply)
  implicit val apiEndpointConfigFormat: JsonFormat[ApiEndpointConfig] = jsonFormat3(ApiEndpointConfig.apply)
  
  implicit val earthquakeQueryStructureFormat: JsonFormat[EarthquakeQueryStructure] = jsonFormat2(EarthquakeQueryStructure.apply)
