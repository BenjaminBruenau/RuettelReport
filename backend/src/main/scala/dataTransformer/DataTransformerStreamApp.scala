package dataTransformer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Sink
import dataTransformer.parser.MappingRulesParser
import dataTransformer.protocol.{DynamicProtocolGenerator, given}
import spray.json.*

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object DataTransformerStreamApp extends App:
  implicit val system: ActorSystem = ActorSystem("AkkaHttpStreamExample")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()

  val apiUrl = "https://jsonplaceholder.typicode.com/todos"

  val httpRequest = HttpRequest(uri = apiUrl)

  val t1 = System.nanoTime


  // Use Akka HTTP to send the request and receive the response
  val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)

  responseFuture.onComplete {
    case Success(response) => streamResponse(response)
    case Failure(ex) =>
      println(s"Request failed: $ex")
      system.terminate()
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






