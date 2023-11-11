package model

import spray.json.*

import scala.util.{Failure, Success, Try}

trait FeatureJsonProtocol extends DefaultJsonProtocol:

  implicit val metadataFormat: RootJsonFormat[Metadata] = jsonFormat6(Metadata.apply)

  implicit val geometryFormat: RootJsonFormat[Geometry] = jsonFormat2(Geometry.apply)

  // more than 22 parameters in Properties
  implicit val propertiesFormat: RootJsonFormat[Properties] = new RootJsonFormat[Properties]:

    override def read(json: JsValue): Properties =
      val fields = json.asJsObject.fields

      //ToDO: REMOVE THIS AFTER CORRECTLY GETTING API SCHEMA
      /*
      Try(
        Properties(fields("mag").convertTo[Double], fields("place").convertTo[Option[String]], fields("time").convertTo[Long], fields("updated").convertTo[Long], fields("tz").convertTo[Option[String]], fields("url").convertTo[String], fields("detail").convertTo[String], fields("felt").convertTo[Option[Int]], fields("cdi").convertTo[Option[Double]], fields("mmi").convertTo[Option[Double]], fields("alert").convertTo[Option[String]], fields("status").convertTo[String], fields("tsunami").convertTo[Int], fields("sig").convertTo[Int], fields("net").convertTo[String], fields("code").convertTo[String], fields("ids").convertTo[String], fields("sources").convertTo[String], fields("types").convertTo[String], fields("nst").convertTo[Option[Int]], fields("dmin").convertTo[Option[Double]], fields("rms").convertTo[Double], fields("gap").convertTo[Option[Double]], fields("magType").convertTo[String], fields("type").convertTo[String], fields("title").convertTo[String])) match {
        case Success(prop) =>
        case Failure(exception) =>
          println(exception.getMessage)
      }
       */


      val properties = Properties(
        fields("mag").convertTo[Double],
        fields("place").convertTo[Option[String]],
        fields("time").convertTo[Long],
        fields("updated").convertTo[Long],
        fields("tz").convertTo[Option[String]],
        fields("url").convertTo[String],
        fields("detail").convertTo[String],
        fields("felt").convertTo[Option[Int]],
        fields("cdi").convertTo[Option[Double]],
        fields("mmi").convertTo[Option[Double]],
        fields("alert").convertTo[Option[String]],
        fields("status").convertTo[String],
        fields("tsunami").convertTo[Int],
        fields("sig").convertTo[Int],
        fields("net").convertTo[String],
        fields("code").convertTo[String],
        fields("ids").convertTo[String],
        fields("sources").convertTo[String],
        fields("types").convertTo[String],
        fields("nst").convertTo[Option[Int]],
        fields("dmin").convertTo[Option[Double]],
        fields("rms").convertTo[Double],
        fields("gap").convertTo[Option[Double]],
        fields("magType").convertTo[String],
        fields("type").convertTo[String],
        fields("title").convertTo[String])
      properties

    override def write(properties: Properties): JsValue =
      JsObject(
        "mag" -> JsNumber(properties.mag),
        "place" -> properties.place.toJson,
        "time" -> JsNumber(properties.time),
        "updated" -> JsNumber(properties.updated),
        "tz" -> properties.tz.toJson,
        "url" -> JsString(properties.url),
        "detail" -> JsString(properties.detail),
        "felt" -> properties.felt.toJson,
        "cdi" -> properties.cdi.toJson,
        "mmi" -> properties.mmi.toJson,
        "alert" -> properties.alert.toJson,
        "status" -> JsString(properties.status),
        "tsunami" -> JsNumber(properties.tsunami),
        "sig" -> JsNumber(properties.sig),
        "net" -> JsString(properties.net),
        "code" -> JsString(properties.code),
        "ids" -> JsString(properties.ids),
        "sources" -> JsString(properties.sources),
        "types" -> JsString(properties.types),
        "nst" -> properties.nst.toJson,
        "dmin" -> properties.dmin.toJson,
        "rms" -> JsNumber(properties.rms),
        "gap" -> properties.gap.toJson,
        "magType" -> JsString(properties.magType),
        "type" -> JsString(properties.`type`),
        "title" -> JsString(properties.title)
      )




  implicit val featureFormat: RootJsonFormat[Feature] = jsonFormat4(Feature.apply)

  implicit val featureCollectionFormat: RootJsonFormat[FeatureCollection] = jsonFormat3(FeatureCollection.apply)

