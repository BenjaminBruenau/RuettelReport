package queryService

import akka.{Done, NotUsed}
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.{Sink, Source}
import api.QueryServiceHttpServer
import commons.message.MessageService
import commons.message.kafka.KafkaMessageService
import spray.json.JsValue

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@main
def queryServiceApp(): Unit =
  implicit val actorSystem: ActorSystem[Any] = ActorSystem(Behaviors.empty, "QueryServiceActorSystem")

  val messageService: MessageService = Try(QueryServiceAppConfig.kafkaBootstrapServers) match
    case Success(source) =>
      KafkaMessageService.createKafkaService(
        QueryServiceAppConfig.kafkaBootstrapServers,
        "my-group-id",
        actorSystem
      )
    case Failure(exception) =>
      // Dummy to consume the stream and discard the elements, so requests will complete even though there is no Kafka Service available (e.g. in the Free Namespace)
      object MessageServiceDummy extends MessageService {
        override def produceMessagesSink(topic: String, messageId: String): Sink[JsValue, Future[Done]] = Sink.ignore
        override def consumeMessagesSource(topic: String): Source[Nothing, NotUsed] = Source.empty
      }
      MessageServiceDummy

    QueryServiceHttpServer.run(messageService)
