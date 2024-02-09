package commons.message.kafka

import akka.actor.typed.ActorSystem
import akka.event.Logging
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorAttributes, Attributes, Supervision}
import akka.{Done, NotUsed}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import commons.message.MessageService
import spray.json.*

import scala.concurrent.Future


class KafkaMessageService(private val producerSettings: ProducerSettings[String, String],
                          private val consumerSettings: ConsumerSettings[String, String]) extends MessageService:

  // Share single producer instance across streams to improve performance
  private val kafkaProducer: org.apache.kafka.clients.producer.Producer[String, String] =
    producerSettings.createKafkaProducer()

  private val resumeOnParsingException = ActorAttributes.supervisionStrategy {
    case _: spray.json.JsonParser.ParsingException => Supervision.Resume
    case _ => Supervision.stop
  }

  def produceMessagesSink(topic: String, messageId: String): Sink[JsValue, Future[Done]] =
    Flow[JsValue]
      .log("publishing message to kafka", m => {
        "topic" -> topic; "message" -> m.toString
      })
      .withAttributes(Attributes.logLevels(onElement = Logging.DebugLevel, onFinish = Logging.DebugLevel, onFailure = Logging.WarningLevel))
      .map(message => ProducerRecord[String, String](topic, messageId, message.toString))
      .toMat(Producer.plainSink(producerSettings.withProducer(kafkaProducer)))(Keep.right)


  def consumeMessagesSource(topic: String): Source[JsValue, Consumer.Control] =
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(topic))
      .map(_.value().parseJson)
      .withAttributes(resumeOnParsingException)
      .log("received message from kafka", m => {
        "topic" -> topic; "message" -> m.toString
      })
      .withAttributes(Attributes.logLevels(onElement = Logging.DebugLevel, onFinish = Logging.DebugLevel, onFailure = Logging.WarningLevel))

object KafkaMessageService:

  def createKafkaService(bootstrapServers: String,
                         groupId: String,
                         system: ActorSystem[Any]): KafkaMessageService =
    // producerProperties: Map[String, String] = Map.empty,
    // consumerProperties: Map[String, String] = Map.empty
    val producerConfig = system.settings.config.getConfig("akka.kafka.producer")

    val consumerConfig = system.settings.config.getConfig("akka.kafka.consumer")

    val producerSettings = ProducerSettings(producerConfig, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)
    //.withProperties(producerProperties)

    val consumerSettings = ConsumerSettings(consumerConfig, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId(groupId)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    //.withProperties(consumerProperties)

    KafkaMessageService(producerSettings, consumerSettings)


