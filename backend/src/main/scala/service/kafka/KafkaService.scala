package service.kafka

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.{ActorAttributes, Supervision}
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import spray.json.*

import scala.concurrent.{ExecutionContext, Future}
class KafkaService(topic: String)(using system: ActorSystem)(using ec: ExecutionContext):
  private val producerConfig = system.settings.config.getConfig("akka.kafka.producer")
  private val consumerConfig = system.settings.config.getConfig("akka.kafka.consumer")

  private val bootstrapServers = "localhost:29092" //ToDo: this should be provided by e.g. an ENV Variable

  private val producerSettings =
    ProducerSettings(producerConfig, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)

  private val consumerSettings =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId("your-group-id")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  // Share single producer instance across streams to improve performance
  private val kafkaProducer: org.apache.kafka.clients.producer.Producer[String, String] =
    producerSettings.createKafkaProducer()

  private val resumeOnParsingException = ActorAttributes.supervisionStrategy {
    case _: spray.json.JsonParser.ParsingException => Supervision.Resume
    case _ => Supervision.stop
  }

  def produceMessage(topic: String, message: JsValue): Future[Done] =
    val record = new ProducerRecord[String, String](topic, message.toString)
    Source.single(record).runWith(Producer.plainSink(producerSettings.withProducer(kafkaProducer)))


  def consumeMessages(topic: String): Source[JsValue, Consumer.Control] =
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(topic))
      .map(_.value().parseJson)
      .withAttributes(resumeOnParsingException)
