package service;

import model.{FeatureCollection, FeatureJsonProtocol}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpResponse

import scala.concurrent.Future

trait HttpServiceInterface extends FeatureJsonProtocol with SprayJsonSupport:
  def getFeatures(startTime: String, endTime: String): Future[FeatureCollection]
  def sendPUT(uri: String, body: String): Future[HttpResponse]

  def sendGET(uri: String): Future[HttpResponse]

