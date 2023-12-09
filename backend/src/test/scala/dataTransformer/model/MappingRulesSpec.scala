package dataTransformer.model

import dataTransformer.model.MappingExpr.{->, ->/}
import dataTransformer.model.executeMapping
import dataTransformer.protocol.DynamicProtocolGenerator
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.JsonFormat

class MappingRulesSpec extends AnyWordSpec with Matchers {

  case class MockDataFormat(data: String)

  given DynamicProtocolGenerator[MockDataFormat] with

    def generateProtocol(mappingRules: MappingRules): JsonFormat[MockDataFormat] = ???

    def renameAttribute(input: MockDataFormat, attribute: String): Option[MockDataFormat] =
      Some(input.copy(data = attribute))

    def extractNestedAttributeValue(input: MockDataFormat, path: String): Option[MockDataFormat] =
      Some(input.copy(data = path))


  "Mapping Expressions" when {
    val renameMappingExpr = MappingExpr("test", "test2", MappingType.FieldRename)
    val extractMappingExpr = MappingExpr("test", "test.nested", MappingType.FieldExtract)

    "initialized" should {

      "contain mapping paths" in {
        renameMappingExpr.from shouldBe a[String]
        renameMappingExpr.to shouldBe a[String]

        extractMappingExpr.from shouldBe a[String]
        extractMappingExpr.to shouldBe a[String]
      }

      "have a Mapping Type" in {
        renameMappingExpr.mappingType should be (MappingType.FieldRename)
        extractMappingExpr.mappingType should be (MappingType.FieldExtract)
      }
    }

    "the mapping is executed" should {

      val data = MockDataFormat("mockdata")

      "return a specific data format type" in {
          renameMappingExpr.executeMapping(data) shouldBe a[Option[MockDataFormat]]
      }

      "handle different mapping types" in {
        renameMappingExpr.executeMapping(data).get.data should be ("test2")
        extractMappingExpr.executeMapping(data).get.data should be ("test.nested")
      }
    }

    "providing an internal DSL" should {

      "define a concise way to create a Rename Mapping Expression" in {
        val renameExpr = "test" -> "test2"
        renameExpr shouldBe a[MappingExpr]
        renameExpr.mappingType should be (MappingType.FieldRename)
        renameExpr.from should be ("test")
        renameExpr.to should be ("test2")
      }

      "define a concise way to create an Extract Mapping Expression" in {
        val extractExpr = "test" ->/ "test2.nested"
        extractExpr shouldBe a[MappingExpr]
        extractExpr.mappingType should be(MappingType.FieldExtract)
        extractExpr.from should be("test")
        extractExpr.to should be("test2.nested")
      }
    }

  }

  "Mapping Rules" should {
    val mappingRules = MappingRules(Vector(
      "abc" -> "def",
      "foo" ->/ "foo.bar"
    ))
    "contain a Vector of Mapping Expressions" in {
      mappingRules.exprs shouldBe a[Vector[MappingExpr]]
    }

  }
}
