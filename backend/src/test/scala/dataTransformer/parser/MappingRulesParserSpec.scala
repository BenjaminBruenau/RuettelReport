package dataTransformer.parser

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dataTransformer.model.MappingType
import dataTransformer.model.MappingRules
class MappingRulesParserSpec extends AnyWordSpec with Matchers {
  val validInput = "{ \"newName\" -> \"name\", \"address\" -> \"address.city\" }"
  val validInputNoCurlyBraces =  "\"newName\" -> \"name\", \"address\" -> \"address.city\""


  val invalidInput = "{ \"newName\" > \"name\","


  "A MappingRules Parser" when {

    "parsing a valid rule String" should {

      val mappingRules = MappingRulesParser.parseMappingRules(validInput).toOption

      "create a Mapping Expression for each mapping defined in the input String" in {
        mappingRules.get.exprs.length should be (2)
      }

      "distinguish between nested fields and field renames" in {
        mappingRules.get.exprs.head.mappingType should be (MappingType.FieldRename)
        mappingRules.get.exprs.tail.head.mappingType should be (MappingType.FieldExtract)
      }

      "parse field names correctly" in {
        mappingRules.get.exprs.head.from should be ("newName")
        mappingRules.get.exprs.head.to should be ("name")
        mappingRules.get.exprs.tail.head.from should be("address")
        mappingRules.get.exprs.tail.head.to should be("address.city")
      }

      "consider curly braces to be optional" in {
        val mappingRules2 = MappingRulesParser.parseMappingRules(validInputNoCurlyBraces).toOption

        mappingRules2.get should equal (mappingRules.get)
      }
    }

    "parsing an invalid rule String" should {
      val invalidMappingRules = MappingRulesParser.parseMappingRules(invalidInput)

      "return an Either Left" in {
        invalidMappingRules shouldBe a [Left[String, MappingRules]]
      }

      "return an error message, indicating the reason for the failure" in {
        invalidMappingRules.left.toOption.get shouldBe a [String]
        invalidMappingRules.left.toOption.get.contains("'->' expected") should be (true)
      }
      
    }
  }
}
