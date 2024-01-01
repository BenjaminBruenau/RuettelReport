package queryBuilder

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import queryBuilder.impl.HttpQueryBuilder

class QueryBuilderSpec extends AnyWordSpec with Matchers {

  "QueryBuilder" should {
    "correctly parse valid JSON" in {
      val validJson =
        """
          |{
          |  "requestOptions": {
          |    "format": {
          |      "type": "type",
          |      "include": true,
          |      "value": "geojson"
          |    },
          |    "endtime": {
          |      "type": "float",
          |      "include": true,
          |      "value": "1.0"
          |    },
          |    "starttime": {
          |      "type": "float",
          |      "include": true,
          |      "value": "2.0"
          |    },
          |    "minmagnitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    },
          |    "maxmagnitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    },
          |    "minlongitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    },
          |    "maxlongitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    },
          |    "minlatitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    },
          |    "maxlatitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    }
          |  },
          |  "api_endpoints": {
          |    "earthquake": {
          |      "url": "https://example.com/api/earthquake",
          |      "method": "GET",
          |      "params": {
          |        "start": "starttime",
          |        "end": "endtime",
          |        "minMag": "minmagnitude",
          |        "maxMag": "maxmagnitude",
          |        "minLon": "minlongitude",
          |        "maxLon": "maxlongitude",
          |        "minLat": "minlatitude",
          |        "maxLat": "maxlatitude"
          |      }
          |    }
          |  }
          |}
          |""".stripMargin

      val queryBuilder = QueryBuilder.fromJson(validJson)
      queryBuilder shouldBe a[HttpQueryBuilder]
    }

    "throw an exception for invalid JSON" in {
      val invalidJson = """{"invalid": "json"}"""

      assertThrows[Exception] {
        QueryBuilder.fromJson(invalidJson)
      }
    }
    "throw an exception for unsupported format" in {
      val invalidFormatJson =
        """
          |{
          |  "requestOptions": {
          |    "format": {
          |      "type": "type",
          |      "include": true,
          |      "value": "unsupported"
          |    },
          |    "endtime": {
          |      "type": "float",
          |      "include": true,
          |      "value": "1.0"
          |    },
          |    "starttime": {
          |      "type": "float",
          |      "include": true,
          |      "value": "2.0"
          |    },
          |    "minmagnitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    },
          |    "maxmagnitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    },
          |    "minlongitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    },
          |    "maxlongitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    },
          |    "minlatitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    },
          |    "maxlatitude": {
          |      "type": "float",
          |      "include": true,
          |      "value": 90.0
          |    }
          |  },
          |  "api_endpoints": {
          |    "earthquake": {
          |      "url": "https://example.com/api/earthquake",
          |      "method": "GET",
          |      "params": {
          |        "start": "starttime",
          |        "end": "endtime",
          |        "minMag": "minmagnitude",
          |        "maxMag": "maxmagnitude",
          |        "minLon": "minlongitude",
          |        "maxLon": "maxlongitude",
          |        "minLat": "minlatitude",
          |        "maxLat": "maxlatitude"
          |      }
          |    }
          |  }
          |}
          |""".stripMargin

      assertThrows[IllegalArgumentException] {
        QueryBuilder.fromJson(invalidFormatJson)
      }
    }
  }
}
