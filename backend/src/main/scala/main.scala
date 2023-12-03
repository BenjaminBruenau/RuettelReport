import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.HttpResponse
import service.HttpServiceInterface
import service.httpServiceBaseImpl.HttpService

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}



@main
def main(): Unit = {
  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "HTTP_SERVICE")
  implicit val executionContext: ExecutionContextExecutor = actorSystem.executionContext



  val httpService: HttpServiceInterface = new HttpService

  val requestFuture: Future[HttpResponse] = httpService.sendGET(
    "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2023-09-10T00:00:00,&endtime=2023-09-10T23:59:59,&minmagnitude=0&orderby=time")

  requestFuture.onComplete {
    case Success(response: HttpResponse) =>
      println("Successfully Loaded Saves")
      println(response.entity)
    case Failure(exception) => println(exception.getMessage)
  }
}