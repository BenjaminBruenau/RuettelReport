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
import commons.queryBuilder.{QueryBuilder, QueryBuilderBackend}
import commons.queryBuilder.model.{QueryRequestStructure, QueryStructureJsonProtocol}
import commons.http.HttpServiceInterface
import commons.http.httpServiceBaseImpl.HttpService
import commons.message.MessageService
import spray.json.*

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success, Try}
import scala.xml.Elem

class DataTransformerHttpController(messageService: MessageService)(implicit executionContext: ExecutionContextExecutor) extends QueryStructureJsonProtocol with SprayJsonSupport with ScalaXmlSupport:
  private val httpService: HttpServiceInterface = new HttpService
  val protocolGeneratorJs: DynamicProtocolGenerator[JsValue] = summon[DynamicProtocolGenerator[JsValue]]
  val protocolGeneratorXml: DynamicProtocolGenerator[Elem] = summon[DynamicProtocolGenerator[Elem]]

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json(500 * 1024) // 500kb ToDo: Decide on a max size, what about logging such big messages?

  val route: Route =
    concat(
      path("api" / "data-transformer") {
        get {
          entity(as[QueryRequestStructure]) {
            queryRequestStructure =>
              val queryBuilder = QueryBuilder(QueryBuilderBackend.HTTP)
              
              queryRequestStructure.mappingRules match
                case None =>
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
                          val kafkaSink = messageService.produceMessagesSink("your_kafka_topic") //ToDo: tenant
                          val responseFlow = Flow[JsValue].alsoToMat(kafkaSink)(Keep.left)

                          Try(apiSource) match
                            case Success(source: Source[JsValue, Future[Any]]) =>
                              complete(source.via(dataTransformFlow).via(responseFlow))
                            case Failure(exception) =>
                              complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        }
      },
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

