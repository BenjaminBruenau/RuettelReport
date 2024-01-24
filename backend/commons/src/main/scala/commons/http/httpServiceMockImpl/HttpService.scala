package commons.http.httpServiceMockImpl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.HttpResponse
import com.typesafe.config.{Config, ConfigFactory}
import commons.model.{Feature, FeatureCollection, Geometry, Metadata, Properties}
import commons.http.HttpServiceInterface

import scala.concurrent.{ExecutionContextExecutor, Future}

class HttpService extends HttpServiceInterface:
  val config: Config = ConfigFactory.load()
  val host: String = config.getString("http.earthquakeHost")

  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "HTTP_SERVICE");
  implicit val executionContext: ExecutionContextExecutor = actorSystem.executionContext


  def getFeatures(startTime: String, endTime: String): Future[FeatureCollection] =
    val feature = Feature(
      "Feature",
      Properties(3.6, Some("8 km NNW of Segundo, Colorado"), 1694389572927L, 1696992504478L, None, "https://earthquake.usgs.gov/earthquakes/eventpage/us7000kuwn", "https://earthquake.usgs.gov/fdsnws/event/1/query?eventid=us7000kuwn&format=geojson", Some(26), Some(5.6), Some(4.672), None, "reviewed", 0, 214, "us", "7000kuwn", ",us7000kuwn,", ",us,", ",dyfi,moment-tensor,origin,phase-data,shakemap,", Some(28), Some(0.075), 0.53, Some(59), "mwr", "earthquake", "M 3.6 - 8 km NNW of Segundo, Colorado"),
      Geometry("Point", Vector(-104.7813, 37.1967, 1.963)),
      "us7000kuwn"
    )
    val metadata = Metadata(
      1,
      "https://localhost:8080",
      "test",
      1,
      "local api",
      1
    )

    Future(FeatureCollection("Feature", metadata, Vector(feature)))


  def sendPUT(uri: String, body: String): Future[HttpResponse] = Future(HttpResponse.apply())

  def sendGET(uri: String): Future[HttpResponse] = Future(HttpResponse.apply())
