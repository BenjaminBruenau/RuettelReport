package queryService.api

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes, HttpRequest}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.headers.*
import akka.http.scaladsl.model.HttpMethods.*
import akka.http.scaladsl.server.Route
import akka.stream.alpakka.json.scaladsl.JsonReader
import akka.stream.scaladsl.{Flow, Keep, Source}
import akka.util.ByteString
import org.slf4j.LoggerFactory
import commons.queryBuilder.{QueryBuilder, QueryBuilderBackend}
import commons.queryBuilder.model.{QueryRequestStructure, QueryStructureJsonProtocol}
import commons.message.MessageService
import spray.json.*

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

class QueryServiceHttpController(messageService: MessageService)(implicit val system: ActorSystem[Nothing], executionContext: ExecutionContextExecutor) extends QueryStructureJsonProtocol with SprayJsonSupport:

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json(50000 * 1024) // 50mb -> query needs to be adjusted if bigger than that

  val route: Route =
    concat(
      path("query-service" / "api" / "query") {
        get {
          handleQueryRequest()
        } ~
        post {
          handleQueryRequest()
        }
      }
    )

  private def handleQueryRequest(): Route =
    entity(as[QueryRequestStructure]) { queryRequestStructure =>
      val queryBuilder = QueryBuilder(QueryBuilderBackend.HTTP)
      queryBuilder.buildQuery(queryRequestStructure.queryStructure, queryRequestStructure.endpoint) match {
        case None =>
          complete(StatusCodes.BadRequest, "Cannot generate Query from provided Structure")
        case Some(externalApiQueryUrl) =>
          val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = externalApiQueryUrl))

          val apiSource = Source.futureSource(responseFuture.map {
            case HttpResponse(StatusCodes.OK, _, entity, _) =>
              entity.dataBytes
            case HttpResponse(status, _, entity, _) =>
              throw new RuntimeException(s"Request failed with status code $status and entitiy: $entity")
          })

          val jsonKafkaFlow = Flow[ByteString].via(JsonReader.select("$.features[*]")).map(_.utf8String.parseJson)
          val jsonResponseFlow = Flow[ByteString].via(jsonStreamingSupport.framingDecoder).map(_.utf8String.parseJson)
          val kafkaSink = messageService.produceMessagesSink("feature_test")

          Try(apiSource) match {
            case Success(source) =>
              val connectedSource = source.alsoToMat(jsonKafkaFlow.toMat(kafkaSink)(Keep.right))(Keep.right)
              complete(connectedSource.via(jsonResponseFlow))
            case Failure(exception) =>
              complete(StatusCodes.InternalServerError, exception.getMessage)
          }
      }
    }


object QueryServiceHttpServer:
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "query-service-http-server")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  def run(messageService: MessageService): Unit =
    val api = QueryServiceHttpController(messageService)
    import queryService.QueryServiceAppConfig
    val route: Route = api.route
    val host = QueryServiceAppConfig.host
    val port = QueryServiceAppConfig.port

    val bindingFuture = Http().newServerAt(host, port).bind(route)
    LoggerFactory.getLogger(this.getClass).info(s"Server started at http://$host:$port/")

