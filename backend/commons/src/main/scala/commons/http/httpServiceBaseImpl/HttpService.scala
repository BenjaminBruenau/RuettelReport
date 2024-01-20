package commons.http.httpServiceBaseImpl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.config.{Config, ConfigFactory}
import commons.http.HttpServiceInterface
import commons.model.*

import scala.concurrent.{ExecutionContextExecutor, Future}

class HttpService extends HttpServiceInterface:
  val config: Config = ConfigFactory.load()
  val host: String = config.getString("http.earthquakeHost")

  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "HTTP_SERVICE");
  implicit val executionContext: ExecutionContextExecutor = actorSystem.executionContext


  def getFeatures(startTime: String, endTime: String): Future[FeatureCollection] =
    val requestFuture: Future[HttpResponse] =
      sendGET(s"https://${host}/fdsnws/event/1/query?format=geojson&starttime=${startTime},&endtime=${endTime},&minmagnitude=0&orderby=time")

    for {
      response <- requestFuture
      features <- Unmarshal(response.entity).to[FeatureCollection]
    } yield features


  def sendPUT(uri: String, body: String): Future[HttpResponse] =
    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.PUT,
        uri = uri,
        entity =
          HttpEntity(ContentTypes.`application/json`, body)
      )
    )

  def sendGET(uri: String): Future[HttpResponse] =
    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.GET,
        uri = uri,
      )
    )
