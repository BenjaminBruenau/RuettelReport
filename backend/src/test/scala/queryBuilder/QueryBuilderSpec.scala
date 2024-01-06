package queryBuilder

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class QueryBuilderSpec extends AnyWordSpec with Matchers {


  "QueryBuilder" should {

    "support the http backend" in {
      val supportedBackend = "HTTP"

      val builder = QueryBuilder(QueryBuilderBackend.valueOf(supportedBackend))

      builder shouldBe a[QueryBuilder]
    }

    "throw exception for unsupported backend" in {
      val unsupportedBackend = "XD"

      val exception = intercept[IllegalArgumentException] {
        QueryBuilder(QueryBuilderBackend.valueOf(unsupportedBackend))
      }

      exception.getMessage shouldBe s"enum case not found: $unsupportedBackend"
    }
  }

}
