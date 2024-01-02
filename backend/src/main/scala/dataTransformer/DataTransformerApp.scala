package dataTransformer

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import dataTransformer.api.DataTransformerHttpServer
import service.message.MessageService
import service.message.kafka.KafkaMessageService


@main
def dataTransformerApp(): Unit =
  implicit val actorSystem: ActorSystem[Any] = ActorSystem(Behaviors.empty, "DataTransformerActorSystem")


  val messageService: MessageService= KafkaMessageService.createKafkaService(
    DataTransformerAppConfig.kafkaBootstrapServers,
    "my-group-id",
    actorSystem
  )


  DataTransformerHttpServer.run(messageService)

