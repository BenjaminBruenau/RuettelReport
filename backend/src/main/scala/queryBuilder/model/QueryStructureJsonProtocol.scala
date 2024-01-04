package queryBuilder.model

import spray.json.*


case class RequestOptions(format: RequestOptionField[String], endtime: RequestOptionField[String], starttime: RequestOptionField[String], minmagnitude: RequestOptionField[Double], maxmagnitude: RequestOptionField[Double], minlongitude: RequestOptionField[Double], maxlongitude: RequestOptionField[Double], minlatitude: RequestOptionField[Double], maxlatitude: RequestOptionField[Double])

case class RequestOptionField[T](`type`: String, include: Boolean, value: Option[T] = None)


case class ApiEndpointConfig(url: String, method: String, params: Map[String, String])
case class QueryStructure(requestOptions: RequestOptions, api_endpoints: Map[String, ApiEndpointConfig])

case class QueryRequestStructure(queryStructure: QueryStructure, endpoint: String, mappingRules: Option[String])

trait QueryStructureJsonProtocol extends DefaultJsonProtocol:
  implicit val requestOptionFieldStringValue: RootJsonFormat[RequestOptionField[String]] = jsonFormat3(RequestOptionField[String].apply)
  implicit val requestOptionFieldDoubleValue: RootJsonFormat[RequestOptionField[Double]] = jsonFormat3(RequestOptionField[Double].apply)
  implicit val requestOptionsFormat: RootJsonFormat[RequestOptions] = jsonFormat9(RequestOptions.apply)
  implicit val apiEndpointConfigFormat: RootJsonFormat[ApiEndpointConfig] = jsonFormat3(ApiEndpointConfig.apply)

  implicit val queryStructureFormat: RootJsonFormat[QueryStructure] = jsonFormat2(QueryStructure.apply)

  implicit val queryRequestStructureFormat: RootJsonFormat[QueryRequestStructure] = jsonFormat3(QueryRequestStructure.apply)