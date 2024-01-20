package api

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.HttpMethods.*
import akka.http.scaladsl.model.headers.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.typesafe.config.{Config, ConfigFactory}
import commons.http.HttpServiceInterface
import commons.http.httpServiceBaseImpl.HttpService
import commons.model.{Feature, FeatureJsonProtocol, Geometry, Properties}
import spray.json.JsValue

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success, Try}

class HttpController extends FeatureJsonProtocol with SprayJsonSupport:
  private val httpService: HttpServiceInterface = new HttpService

  val route: Route =
    concat(
      path("api" / "example") {
        get {
          complete {
            Feature(
              "Feature",
              Properties(3.6, Some("8 km NNW of Segundo, Colorado"), 1694389572927L, 1696992504478L, None, "https://earthquake.usgs.gov/earthquakes/eventpage/us7000kuwn", "https://earthquake.usgs.gov/fdsnws/event/1/query?eventid=us7000kuwn&format=geojson", Some(26), Some(5.6), Some(4.672), None, "reviewed", 0, 214, "us", "7000kuwn", ",us7000kuwn,", ",us,", ",dyfi,moment-tensor,origin,phase-data,shakemap,", Some(28), Some(0.075), 0.53, Some(59), "mwr", "earthquake", "M 3.6 - 8 km NNW of Segundo, Colorado"),
              Geometry("Point", Vector(-104.7813, 37.1967, 1.963)),
              "us7000kuwn"
            )
          }
        }
      },
      path("api" / "example") {
        put {
          entity(as[JsValue]) {
            json =>
              Try(json.convertTo[Feature]) match
                case Success(feature: Feature) =>
                  val newFeature = feature.copy(`type` = "updatedFeature")
                  complete {
                    newFeature
                  }
                case Failure(exception) =>
                  println(exception.getMessage)
                  complete(StatusCodes.BadRequest, "Invalid Feature Body")
          }
        }
      },
      path("api" / "event") {
        parameters("starttime", "endtime") { (startTime, endTime) =>
          onComplete(httpService.getFeatures(startTime, endTime)) {
            case Success(featureCollection) =>
              respondWithHeaders(
                // Allow all origins (you might want to restrict this in production)
                `Access-Control-Allow-Origin`.*,
                // Allow the headers that might be sent by the client during the actual request
                `Access-Control-Allow-Headers`("Content-Type", "Authorization"),
                // Allow all HTTP methods (you might want to restrict this in production)
                `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)
              ) {
                complete {
                  featureCollection.copy(`type` = "updatedFeatureCollection")
                }
              }
            case Failure(exception) =>
              println(exception.getMessage)
              complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        }
      }
    )



object HttpServer:

  @main def HttpMain(): Unit =
    val config: Config = ConfigFactory.load()

    val host: String = config.getString("http.host")
    val port: String = config.getString("http.port")

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "http-server")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val api: HttpController = HttpController()

    val route: Route = api.route


    val bindingFuture = Http().newServerAt(host, port.toInt).bind(route)

    println("Server started at http://" + host + ":" + port + "\n Press RETURN to stop...")




