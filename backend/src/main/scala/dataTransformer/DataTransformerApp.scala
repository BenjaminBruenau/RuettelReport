package dataTransformer

import dataTransformer.parser.MappingRulesParser

import dataTransformer.protocol.*
import dataTransformer.protocol.given
import spray.json.{JsValue, *}

object DataTransformerApp extends App:
  //val userMappingRules = "{ \"fieldA\" -> \"newFieldA\", \"fieldB\" -> \"nested.fieldB\" }"
  //val inputJson = """{ "fieldA": "valueA", "fieldB": { "nested": { "fieldB": "valueB" } } }"""

  transformInput
  // ToDo: match input e.g. NodeSeq => inject XML Generator, JsValue => inject Json Generator

  private def transformInput(using protocolGenerator: DynamicProtocolGenerator[JsValue]): Unit =
    val userMappingRules = "{ \"newName\" -> \"name\", \"address\" -> \"address.city\", \"old_address\" -> \"address\", \"test.a\" -> \"address.zip\", \"test.b\" -> \"address\" }"
    val inputJson = """{"name": "John", "age": 25, "address": {"city": "New York", "zip": "10001"}}"""

    val parseResult = MappingRulesParser.parseMappingRules(userMappingRules)

    parseResult match {
      case Right(rules) =>
        val dynamicProtocol = protocolGenerator.generateProtocol(rules)

        val transformedJson = DataTransformer.transform(inputJson.parseJson, dynamicProtocol)

        println(s"Transformed JSON: $transformedJson")

      case Left(errorMessage) => println(s"Error: $errorMessage")
    }