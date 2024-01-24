package dataTransformer.protocol

import dataTransformer.model.MappingExpr.{->, ->/}
import dataTransformer.model.MappingRules
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.*

class DynamicProtocolGeneratorSpec extends AnyWordSpec with Matchers {
  val inputJson = """{"def": "test", "foo": {"bar": "nested-test"}}"""

  "A Dynamic Protocol Generator" when {

    "providing an existing type class instance for JSON input" should {
      val protocolGenerator: DynamicProtocolGenerator[JsValue] = summon[DynamicProtocolGenerator[JsValue]]
      val mappingRules = MappingRules(Vector(
        "def" -> "abc",
        "foo.bar" ->/ "foo"
      ))

      "generate a spray-json Protocol to transform one JSON structure into another based on Mapping Rules" in {
        val protocol = protocolGenerator.generateProtocol(mappingRules)

        protocol shouldBe a[JsonFormat[JsValue]]
      }

      "include logic to execute the mapping and transform the input JSON into a desired output JSON" in {
        val protocol = protocolGenerator.generateProtocol(mappingRules)

        val transformedJson = protocol.write(inputJson.parseJson)
        transformedJson shouldBe a[JsValue]

        transformedJson.toString should be ("{\"abc\":\"test\",\"foo\":\"nested-test\"}")
      }

      "handle multiple mappings for nested fields of a specific parent field in the output structure" in {
        val inputJson = """{"def": "test", "foo": {"bar": "nested-test", "foo": 0}}"""
        val protocol = protocolGenerator.generateProtocol(MappingRules(Vector(
          "def" -> "abc",
          "foo.bar" ->/ "foo.a",
          "foo" ->/ "foo.b"
        )))

        val transformedJson = protocol.write(inputJson.parseJson)
        transformedJson.toString should be ("{\"abc\":\"test\",\"foo\":{\"a\":\"nested-test\",\"b\":{\"bar\":\"nested-test\",\"foo\":0}}}")
      }

      /*
          ToDo: Tests for this case
          where a field is both assigned a non Object value aswell as object values (nested fields in this case)
      val protocol = protocolGenerator.generateProtocol(MappingRules(Vector(
        "def" -> "abc",
        "foo.bar" ->/ "foo",
        "foo.bar" ->/ "foo.a",
        "foo" ->/ "foo.b"
      )))
       */

      "does not provide support for read operations on the generated protocol" in {
        val protocol = protocolGenerator.generateProtocol(mappingRules)
        an[UnsupportedOperationException] should be thrownBy {
          protocol.read(JsString("test"))
        }
      }

    }

    "extended with additional Data Formats" should {

    }
  }
}
