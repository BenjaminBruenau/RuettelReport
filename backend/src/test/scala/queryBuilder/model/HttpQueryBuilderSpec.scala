package queryBuilder.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import queryBuilder.impl.HttpQueryBuilder

class HttpQueryBuilderSpec extends AnyWordSpec with Matchers {

  "HttpQueryBuilder" should {
    "correctly build a query with valid configuration" in {
      val validConfig =
        EarthquakeQueryStructure(
          RequestOptions(
            RequestOptionField("type", include = true, Some("geojson")),
            RequestOptionField("float", include = true, Some("1.0")),
            RequestOptionField("float", include = true, Some("2.0")),
            RequestOptionField("float", include = true, Some(90.0)),
            RequestOptionField("float", include = true, Some(90.0)),
            RequestOptionField("float", include = true, Some(90.0)),
            RequestOptionField("float", include = true, Some(90.0)),
            RequestOptionField("float", include = true, Some(90.0)),
            RequestOptionField("float", include = true, Some(90.0))
          ),
          Map(
            "earthquake" -> ApiEndpointConfig(
              "https://example.com/api/earthquake",
              "GET",
              Map(
                "start" -> "starttime",
                "end" -> "endtime",
                "minMag" -> "minmagnitude",
                "maxMag" -> "maxmagnitude",
                "minLon" -> "minlongitude",
                "maxLon" -> "maxlongitude",
                "minLat" -> "minlatitude",
                "maxLat" -> "maxlatitude"
              )
            )
          )
        )

      val httpQueryBuilder = new HttpQueryBuilder(validConfig)
      val result = httpQueryBuilder.buildQuery("earthquake")

      result shouldBe "https://example.com/api/earthquake?minMag=90.0&maxLat=90.0&maxLon=90.0&minLat=90.0&end=1.0&start=2.0&maxMag=90.0&minLon=90.0"
    }

    // Add test when Error Handling is implemented
  }
}
