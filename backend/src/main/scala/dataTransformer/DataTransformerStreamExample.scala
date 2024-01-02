package dataTransformer

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import com.typesafe.config.{Config, ConfigFactory}
import dataTransformer.model.MappingExpr.{->, ->*, ->/}
import dataTransformer.model.MappingRules
import dataTransformer.parser.MappingRulesParser
import dataTransformer.protocol.{DynamicProtocolGenerator, given}
import service.http.HttpServiceInterface
import service.http.httpServiceBaseImpl.HttpService
import service.message.kafka.KafkaMessageService
import spray.json.*

import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success, Try}
import scala.xml.Elem

class DataTransformerStreamHttpController extends DefaultJsonProtocol with SprayJsonSupport with ScalaXmlSupport:
  private val httpService: HttpServiceInterface = new HttpService
  val protocolGeneratorJs: DynamicProtocolGenerator[JsValue] = summon[DynamicProtocolGenerator[JsValue]]
  val protocolGeneratorXml: DynamicProtocolGenerator[Elem] = summon[DynamicProtocolGenerator[Elem]]
  val userMappingRules = "{ \"userId\" -> \"newId\" }"
  val parseResult = MappingRulesParser.parseMappingRules(userMappingRules).toOption
  val dbMappingRules = MappingRules(Vector("features" ->* Vector("s.@id" -> "properties.id", "s.@eva" -> "properties.eva", "s.ar.@ct" ->/ "properties.ct")))

  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "http-server")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()

  val kafkaService: KafkaMessageService = KafkaMessageService.createKafkaService(DataTransformerAppConfig.kafkaBootstrapServers, "test", system)

  val route: Route =
    concat(
      path("api" / "data-transformer") {
        get {
          val apiUrl = "https://jsonplaceholder.typicode.com/todos"
          val httpRequest = HttpRequest(uri = apiUrl)
          val responseFuture: Future[HttpResponse] = httpService.sendGET(apiUrl)

          val dynamicProtocol = protocolGeneratorJs.generateProtocol(parseResult.get)

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
      path("api" / "data-transformer-message") {
        get {
          val apiUrl = "https://jsonplaceholder.typicode.com/todos"
          val httpRequest = HttpRequest(uri = apiUrl)
          val responseFuture: Future[HttpResponse] = httpService.sendGET(apiUrl)

          val dynamicProtocol = protocolGeneratorJs.generateProtocol(parseResult.get)

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
          val kafkaSink = kafkaService.produceMessagesSink("your_kafka_topic")
          val responseFlow = Flow[JsValue].alsoToMat(kafkaSink)(Keep.left)

          Try(apiSource) match
            case Success(source: Source[JsValue, Future[Any]]) =>
              complete(source.via(dataTransformFlow).via(responseFlow))
            case Failure(exception) =>
              complete(StatusCodes.InternalServerError, exception.getMessage)

        }
      },
      path("api" / "db") {
        get {
          val authHeaders = Seq(
            RawHeader("DB-Api-Key", "4ebc39496d94c57336f6ec31c69c5239"),
            RawHeader("DB-Client-Id", "7f499ebd9110a123bcf7e6a005595cb1")
          )
          val apiUrlChanges = "https://apis.deutschebahn.com/db-api-marketplace/apis/timetables/v1/fchg/8011160"
          val apiUrl = "https://apis.deutschebahn.com/db-api-marketplace/apis/timetables/v1/plan/8011160/231217/11"
          val request = HttpRequest(
            method = HttpMethods.GET,
            uri = Uri(apiUrlChanges),
          ).withHeaders(authHeaders)

          val responseFuture: Future[HttpResponse] = Http().singleRequest(request)

          val dynamicProtocol = protocolGeneratorXml.generateProtocol(dbMappingRules)

          val dataTransformFlow = Flow[Elem].map(chunk => DataTransformer.transform(chunk, dynamicProtocol))

          val apiSource = Source.futureSource(responseFuture.flatMap {
            case HttpResponse(StatusCodes.OK, _, entity, _) =>
              val strictEntity: Future[HttpEntity.Strict] = entity.toStrict(3.seconds)
              strictEntity.map { e =>
                e.dataBytes.map(bytes =>
                  scala.xml.XML.loadString(bytes.utf8String))
              }
            case HttpResponse(status, _, _, _) =>
              throw new RuntimeException(s"Request failed with status code $status")
          })

          Try(apiSource) match
            case Success(source: Source[Elem, Future[Any]]) =>
              complete(source.via(dataTransformFlow))
            case Failure(exception) =>
              complete(StatusCodes.InternalServerError, exception.getMessage)
        }
      },
    )

object DataTransformerHttpServerStream:
  @main def DataTransformerHttpMain(): Unit =
    val config: Config = ConfigFactory.load()

    val host: String = config.getString("http.host")
    val port: String = config.getString("http.port")

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "http-server")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val api = DataTransformerStreamHttpController()

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






