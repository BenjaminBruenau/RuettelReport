package dataTransformer

import dataTransformer.parser.MappingRulesParser
import dataTransformer.protocol.*
import dataTransformer.protocol.given
import spray.json.{JsValue, *}
import dataTransformer.model.{MappingRules, MappingExpr}
import scala.xml.Elem
import dataTransformer.model.MappingExpr.{->, ->/, ->*}
object DataTransformerApp extends App:
  //val userMappingRules = "{ \"fieldA\" -> \"newFieldA\", \"fieldB\" -> \"nested.fieldB\" }"
  //val inputJson = """{ "fieldA": "valueA", "fieldB": { "nested": { "fieldB": "valueB" } } }"""

  transformInput
  transformXML
  // ToDo: match input e.g. NodeSeq => inject XML Generator, JsValue => inject Json Generator

  private def transformInput(using protocolGenerator: DynamicProtocolGenerator[JsValue]): Unit =
    val userMappingRules = "{ \"name\" -> \"newName\", \"address.city\" -> \"address\", \"address\" -> \"old_address\", \"address.zip\" -> \"test.a\", \"address\" -> \"test.b\"  }"
    val inputJson = """{"name": "John", "age": 25, "address": {"city": "New York", "zip": "10001"}}"""

    val parseResult = MappingRulesParser.parseMappingRules(userMappingRules)

    parseResult match {
      case Right(rules) =>
        val dynamicProtocol = protocolGenerator.generateProtocol(rules)

        val transformedJson = DataTransformer.transform(inputJson.parseJson, dynamicProtocol)

        println(s"Transformed JSON: $transformedJson")

      case Left(errorMessage) => println(s"Error: $errorMessage")
    }


  private def transformXML(using protocolGenerator: DynamicProtocolGenerator[Elem]): Unit =
    val mappingRules = MappingRules(Vector("features" ->* Vector("s.@id" -> "properties.id", "s.@eva" -> "properties.eva", "s.ar.@ct" ->/ "properties.ct")))
    val xmlString =
      """<timetable station="Berlin Hbf" eva="8011160">
        |  <s id="1" eva="123">
        |    <ar ct="2023-12-10T12:00:00"/>
        |  </s>
        |  <s id="2" eva="456">
        |    <ar ct="2023-12-10T13:00:00"/>
        |  </s>
        |</timetable>""".stripMargin
    val xml = scala.xml.XML.loadString(xmlString)
    val geoJson = protocolGenerator.generateProtocol(mappingRules).write(xml)
    
    println(geoJson.prettyPrint)

