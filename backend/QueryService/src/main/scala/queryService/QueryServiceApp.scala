package queryService

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import api.QueryServiceHttpServer
import commons.message.MessageService
import commons.message.kafka.KafkaMessageService

@main
def dataTransformerApp(): Unit =
  implicit val actorSystem: ActorSystem[Any] = ActorSystem(Behaviors.empty, "QueryServiceActorSystem")


  val messageService: MessageService= KafkaMessageService.createKafkaService(
    QueryServiceAppConfig.kafkaBootstrapServers,
    "my-group-id",
    actorSystem
  )


  QueryServiceHttpServer.run(messageService)

