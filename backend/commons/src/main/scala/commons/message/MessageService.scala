package commons.message

import akka.Done
import akka.stream.scaladsl.{Sink, Source}
import spray.json.JsValue

import scala.concurrent.Future

trait MessageService:
  def produceMessagesSink(topic: String): Sink[JsValue, Future[Done]]

  def consumeMessagesSource(topic: String): Source[JsValue, Any]
