package dataTransformer

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.{Config, ConfigFactory}
import dataTransformer.parser.MappingRulesParser
import dataTransformer.protocol.{DynamicProtocolGenerator, given}
import service.HttpServiceInterface
import service.httpServiceBaseImpl.HttpService
import spray.json.*

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success, Try}


class DataTransformerHttpController extends DefaultJsonProtocol with SprayJsonSupport:
  private val httpService: HttpServiceInterface = new HttpService
  val protocolGenerator: DynamicProtocolGenerator[JsValue] = summon[DynamicProtocolGenerator[JsValue]]
  val userMappingRules = "{ \"newId\" -> \"userId\" }"
  val parseResult = MappingRulesParser.parseMappingRules(userMappingRules).toOption

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "http-server")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()

  val route: Route =
    concat(
      path("api" / "data-transformer") {
        get {
          val apiUrl = "https://jsonplaceholder.typicode.com/todos"
          val httpRequest = HttpRequest(uri = apiUrl)
          val responseFuture: Future[HttpResponse] = httpService.sendGET(apiUrl)

          val dynamicProtocol = protocolGenerator.generateProtocol(parseResult.get)

          val dataTransformFlow = Flow[JsValue].map(chunk => DataTransformer.transform(chunk, dynamicProtocol))

          val apiSource = Source.futureSource(responseFuture.map {
            case HttpResponse(StatusCodes.OK, _, entity, _) =>
              entity.dataBytes
                .via(jsonStreamingSupport.framingDecoder)
                .map(_.utf8String.parseJson)
            case HttpResponse(status, _, _, _) =>
              throw new RuntimeException(s"Request failed with status code $status")
          })

          Try(apiSource) match
            case Success(source: Source[JsValue, Future[Any]]) =>
              complete(source.via(dataTransformFlow))
            case Failure(exception) =>
              complete(StatusCodes.InternalServerError, exception.getMessage)
        }
      },
    )

object DataTransformerHttpServer:
  @main def DataTransformerHttpMain(): Unit =
    val config: Config = ConfigFactory.load()

    val host: String = config.getString("http.host")
    val port: String = config.getString("http.port")

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "http-server")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val api = DataTransformerHttpController()

    val route: Route = api.route


    val bindingFuture = Http().newServerAt(host, port.toInt).bind(route)

    println("Server started at http://" + host + ":" + port + "\n Press RETURN to stop...")

    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

object DataTransformerStreamApp extends App:
  import akka.actor.ActorSystem
  implicit val system: ActorSystem = ActorSystem("AkkaHttpStreamExample")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()

  val apiUrl = "https://jsonplaceholder.typicode.com/todos"

  val httpRequest = HttpRequest(uri = apiUrl)

  val t1 = System.nanoTime


  // Use Akka HTTP to send the request and receive the response
  val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)

  /*
  responseFuture.onComplete {
    case Success(response) => streamResponse(response)
    case Failure(ex) =>
      println(s"Request failed: $ex")
      system.terminate()
  }

   */
  val protocolGenerator: DynamicProtocolGenerator[JsValue] = summon[DynamicProtocolGenerator[JsValue]]
  val userMappingRules = "{ \"newId\" -> \"userId\" }"
  val parseResult = MappingRulesParser.parseMappingRules(userMappingRules)

  parseResult match {
    case Right(rules) =>
      val dynamicProtocol = protocolGenerator.generateProtocol(rules)

      val dataTransformFlow = Flow[JsValue].map(chunk => DataTransformer.transform(chunk, dynamicProtocol))

      val responseStream = Source.futureSource(responseFuture.map {
        case HttpResponse(StatusCodes.OK, _, entity, _) =>
          entity.dataBytes
            .via(jsonStreamingSupport.framingDecoder)
            .map(_.utf8String.parseJson)
        case HttpResponse(status, _, _, _) =>
          throw new RuntimeException(s"Request failed with status code $status")
      })
      val sink = Sink.foreach[JsValue](
        chunk =>
          println(s"Transformed JSON: $chunk")
      )

      val streamGraph = responseStream.via(dataTransformFlow).to(sink)
      streamGraph.run().onComplete {
        case Success(_) =>
          println("Stream completed successfully")
          val duration = (System.nanoTime - t1) / 1e9d
          println(s"Duration: $duration s")
          system.terminate()
        case Failure(exception) =>
          system.terminate()
      }

    case Left(errorMessage) => println(s"Error: $errorMessage")
  }





  private def streamResponse(response: HttpResponse)(using protocolGenerator: DynamicProtocolGenerator[JsValue]): Unit =
    // Create a Source from the response entity
    val streamSource = response.entity.dataBytes
      .via(jsonStreamingSupport.framingDecoder)
      .map(bytes => bytes.utf8String.parseJson)

    val userMappingRules = "{ \"newId\" -> \"userId\" }"
    val parseResult = MappingRulesParser.parseMappingRules(userMappingRules)
    parseResult match {
      case Right(rules) =>
        val dynamicProtocol = protocolGenerator.generateProtocol(rules)


        // Define a Sink to consume the stream
        val sink = Sink.foreach[JsValue](
          chunk =>
            //println(chunk.utf8String)
            val transformedJson = DataTransformer.transform(chunk, dynamicProtocol)
            println(s"Transformed JSON: $transformedJson")
        )
        // Connect the Source to the Sink to create a stream
        streamSource.runWith(sink).onComplete {
          case Success(_) =>
            println("Stream completed successfully")
            val duration = (System.nanoTime - t1) / 1e9d
            println(s"Duration: $duration s")
            system.terminate()
          case Failure(exception) =>
            println(s"Stream failed with: $exception")
            system.terminate()
        }
      case Left(errorMessage) => println(s"Error: $errorMessage")
    }






