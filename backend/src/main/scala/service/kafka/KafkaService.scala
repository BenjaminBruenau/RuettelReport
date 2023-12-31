package service.kafka

import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorAttributes, Supervision}
import akka.{Done, NotUsed}
import org.apache.kafka.clients.producer.ProducerRecord
import spray.json.*

import scala.concurrent.Future
class KafkaService(private val producerSettings: ProducerSettings[String, String],
                   private val consumerSettings: ConsumerSettings[String, String]):

  // Share single producer instance across streams to improve performance
  private val kafkaProducer: org.apache.kafka.clients.producer.Producer[String, String] =
    producerSettings.createKafkaProducer()

  private val resumeOnParsingException = ActorAttributes.supervisionStrategy {
    case _: spray.json.JsonParser.ParsingException => Supervision.Resume
    case _ => Supervision.stop
  }

  def produceMessage(topic: String): Sink[JsValue, Future[Done]] =
    Flow[JsValue]
      .log("publishing message to kafka", m => {
        "topic" -> topic; "message" -> m.toString
      })
      //.withAttributes(Attributes.logLevels(onElement = Logging.DebugLevel, onFinish = Logging.WarningLevel, onFailure = Logging.WarningLevel))
      .map(message => ProducerRecord[String, String](topic, message.toString))
      .toMat(Producer.plainSink(producerSettings.withProducer(kafkaProducer)))(Keep.right)


  def consumeMessages(topic: String): Source[JsValue, Consumer.Control] =
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(topic))
      .map(_.value().parseJson)
      .withAttributes(resumeOnParsingException)
      .log("received message from kafka", m => {
        "topic" -> topic;
        "message" -> m.toString
      })


/*
trait KafkaServiceComponent {
val kafkaService: KafkaService
}

class KafkaServiceComponentImpl extends KafkaServiceComponent {
val kafkaService: KafkaService = new KafkaService()
}


// Singleton to share e.g. Kafka Producer instances across streams
object KafkaMessageService extends KafkaServiceComponent {

val kafkaService: KafkaService = new KafkaServiceComponentImpl().kafkaService
}


*/


