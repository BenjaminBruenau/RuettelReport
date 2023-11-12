package api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{ContentTypes, MessageEntity, StatusCodes}
import akka.http.scaladsl.server.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.testkit.ScalatestRouteTest
import model.{Feature, Geometry, Properties, FeatureJsonProtocol}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HttpServerSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with ScalaFutures with SprayJsonSupport with FeatureJsonProtocol {


  "A HttpServer" when {
    val httpServer = new HttpController
    lazy val routes = httpServer.route
    val base = "/api"
    val exampleFeature = Feature(
      "Feature",
      Properties(3.6, Some("8 km NNW of Segundo, Colorado"), 1694389572927L, 1696992504478L, None, "https://earthquake.usgs.gov/earthquakes/eventpage/us7000kuwn", "https://earthquake.usgs.gov/fdsnws/event/1/query?eventid=us7000kuwn&format=geojson", Some(26), Some(5.6), Some(4.672), None, "reviewed", 0, 214, "us", "7000kuwn", ",us7000kuwn,", ",us,", ",dyfi,moment-tensor,origin,phase-data,shakemap,", Some(28), Some(0.075), 0.53, Some(59), "mwr", "earthquake", "M 3.6 - 8 km NNW of Segundo, Colorado"),
      Geometry("Point", Vector(-104.7813, 37.1967, 1.963)),
      "us7000kuwn"
    )

    "exposing an example Endpoint to GET a single Feature (GET /example)" should {

      "return a Feature" in {
        Get(base + "/example") ~> routes ~> check {
          responseAs[Feature] shouldBe a[Feature]
        }
      }

    }

    "exposing an example Endpoint to PUT a single Feature and return the updated result (PUT /example)" should {

      "return a Feature with an updated `type` attribute" in {
        val entity = Marshal(exampleFeature).to[MessageEntity].futureValue
        Put(base + "/example").withEntity(entity) ~> routes ~> check {
          responseAs[Feature] shouldBe a[Feature]
          responseAs[Feature].`type` should be("updatedFeature")
        }
      }

    }





  }
}
