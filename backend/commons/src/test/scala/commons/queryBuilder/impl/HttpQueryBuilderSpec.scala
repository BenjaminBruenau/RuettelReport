package commons.queryBuilder.impl

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import commons.queryBuilder.model.{ApiEndpointConfig, QueryStructure, RequestOptionField, RequestOptions}

class HttpQueryBuilderSpec extends AnyWordSpec with Matchers {

  val queryStructure: QueryStructure =
    QueryStructure(
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

  val smallQueryStructure: QueryStructure =
    QueryStructure(
      RequestOptions(
        RequestOptionField("type", include = false, Some("geojson")),
        RequestOptionField("float", include = false, Some("1.0")),
        RequestOptionField("float", include = false, Some("2.0")),
        RequestOptionField("float", include = false, Some(90.0)),
        RequestOptionField("float", include = false, Some(90.0)),
        RequestOptionField("float", include = false, Some(90.0)),
        RequestOptionField("float", include = true),
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

  "HttpQueryBuilder" when {
    val queryBuilder = HttpQueryBuilder()

    "building a query" should {

      "map parameter names correctly" in {
        val query = queryBuilder.buildQuery(smallQueryStructure, Some("earthquake"))

        query.get should not include "maxlatitude"
      }

      "only include parameters that are supposed to be included" in {
        val query = queryBuilder.buildQuery(smallQueryStructure, Some("earthquake"))

        query.get should not include "format"
      }

      "omit parameters without a value" in {
        val query = queryBuilder.buildQuery(smallQueryStructure, Some("earthquake"))

        query.get should not include "maxLon"
      }

      "build query with endpoint Option" in {
        val result = queryBuilder.buildQuery(smallQueryStructure, Some("earthquake"))

        result shouldBe Some("https://example.com/api/earthquake?minLat=90.0&maxLat=90.0")
      }

      "build query with endpoint String" in {
        val result = queryBuilder.buildQuery(smallQueryStructure, "earthquake")

        result shouldBe Some("https://example.com/api/earthquake?minLat=90.0&maxLat=90.0")
      }

      "correctly build a query with valid configuration" in {
        val result = queryBuilder.buildQuery(queryStructure, "earthquake")

        result shouldBe Some("https://example.com/api/earthquake?minMag=90.0&maxLat=90.0&maxLon=90.0&minLat=90.0&end=1.0&start=2.0&maxMag=90.0&minLon=90.0")
      }
    }





    // Add test when Error Handling is implemented
  }
}
