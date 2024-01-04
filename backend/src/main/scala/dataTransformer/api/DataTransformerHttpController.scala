package dataTransformer.api

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Keep, Source}
import dataTransformer.parser.MappingRulesParser
import dataTransformer.protocol.{DynamicProtocolGenerator, given}
import dataTransformer.{DataTransformer, DataTransformerAppConfig}
import org.slf4j.LoggerFactory
import queryBuilder.{QueryBuilder, QueryBuilderBackend}
import queryBuilder.model.{QueryRequestStructure, QueryStructureJsonProtocol}
import service.http.HttpServiceInterface
import service.http.httpServiceBaseImpl.HttpService
import service.message.MessageService
import spray.json.*

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success, Try}
import scala.xml.Elem

class DataTransformerHttpController(messageService: MessageService)(implicit executionContext: ExecutionContextExecutor) extends QueryStructureJsonProtocol with SprayJsonSupport with ScalaXmlSupport:
  private val httpService: HttpServiceInterface = new HttpService
  val protocolGeneratorJs: DynamicProtocolGenerator[JsValue] = summon[DynamicProtocolGenerator[JsValue]]
  val protocolGeneratorXml: DynamicProtocolGenerator[Elem] = summon[DynamicProtocolGenerator[Elem]]

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

  val route: Route =
    concat(
      path("api" / "data-transformer") {
        get {
          entity(as[QueryRequestStructure]) {
            queryRequestStructure =>
              val queryBuilder = QueryBuilder(QueryBuilderBackend.HTTP)
              
              queryRequestStructure.mappingRules match
                case None =>
                  // DO same procedure as below without data transformation
                  // or redirect to query service
                  // or cancel since no mapping rules provided -> nothing to transform
                  complete(StatusCodes.BadRequest, "No mapping rules provided")

                case Some(rules) =>
                  val parseResult = MappingRulesParser.parseMappingRules(rules)

                  parseResult match
                    case Left(parseFailureReason) => complete(StatusCodes.BadRequest, parseFailureReason)
                    case Right(mappingRules) =>
                      val dynamicProtocol = protocolGeneratorJs.generateProtocol(mappingRules)

                      queryBuilder.buildQuery(queryRequestStructure.queryStructure, queryRequestStructure.endpoint) match
                        case None => complete(StatusCodes.BadRequest, "Cannot generate Query from provided Structure")
                        case Some(externalApiQueryUrl) =>
                          val responseFuture: Future[HttpResponse] = httpService.sendGET(externalApiQueryUrl)

                          val dataTransformFlow = Flow[JsValue].map(chunk => DataTransformer.transform(chunk, dynamicProtocol))

                          val apiSource = Source.futureSource(responseFuture.map {
                            case HttpResponse(StatusCodes.OK, _, entity, _) =>
                              entity.dataBytes
                                .via(jsonStreamingSupport.framingDecoder)
                                .map(_.utf8String.parseJson)
                            case HttpResponse(status, _, _, _) =>
                              throw new RuntimeException(s"Request failed with status code $status")
                          })

                          // Branch the flow to send to Kafka and complete the request
                          val kafkaSink = messageService.produceMessagesSink("your_kafka_topic") //ToDo: generate appropriate topic name (tenant + endpoint + requestTime)
                          val responseFlow = Flow[JsValue].alsoToMat(kafkaSink)(Keep.left)

                          Try(apiSource) match
                            case Success(source: Source[JsValue, Future[Any]]) =>
                              complete(source.via(dataTransformFlow).via(responseFlow))
                            case Failure(exception) =>
                              complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        }
      }
    )


object DataTransformerHttpServer:
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "data-transformer-http-server")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  def run(messageService: MessageService): Unit =
    val api = DataTransformerHttpController(messageService)

    val route: Route = api.route
    val host = DataTransformerAppConfig.host
    val port = DataTransformerAppConfig.port

    val bindingFuture = Http().newServerAt(host, port).bind(route)
    LoggerFactory.getLogger(this.getClass).info(s"Server started at http://$host:$port/")

    // ToDo: remove shutdown on return logic
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
/*
ToDo:
- split up into micro services
- implement api for data transformer and regular query
- decide how to submit mapping rules and query structure
  - could send both in body, as one structure
  - could generate query beforehand and add as queryParameter to new request
  - put mapping rules in query parameter

- should the mapping rules be send every time? how much overhead is generated through always parsing them?
*/
