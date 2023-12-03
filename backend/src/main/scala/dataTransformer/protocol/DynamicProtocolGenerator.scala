package dataTransformer.protocol

import dataTransformer.model.{MappingRules, executeMapping}
import spray.json.{JsObject, JsValue, JsonFormat}

trait DynamicProtocolGenerator[T]:
  def generateProtocol(mappingRules: MappingRules): JsonFormat[T]

  def renameAttribute(input: T, attribute: String): Option[T]
  def extractNestedAttributeValue(input: T, path: String): Option[T]


given DynamicProtocolGenerator[JsValue] with
  def generateProtocol(mappingRules: MappingRules): JsonFormat[JsValue] = new JsonFormat[JsValue] {
    def read(json: JsValue): JsValue = throw new UnsupportedOperationException("Reading not supported")

    def write(input: JsValue): JsValue = 
      val transformedFields = for {
        rule <- mappingRules.exprs
        value <- rule.executeMapping(input)
      } yield rule.from -> value
      
      JsObject(transformedFields: _*)
  }
  
  def renameAttribute(input: JsValue, attribute: String): Option[JsValue] =
    input.asJsObject.fields.get(attribute)

  def extractNestedAttributeValue(input: JsValue, path: String): Option[JsValue] =
    val keyVector = path.split("\\.").toVector

    def extract(json: JsValue, keys: Vector[String]): Option[JsValue] = keys match {
      case head +: tail =>
        json.asJsObject.fields.get(head).flatMap {
          case nestedObject: JsObject => extract(nestedObject, tail)
          case value: JsValue =>
            // When the path is not fully traversed but value is found or the desired nested attribute value is not an object
            if (tail.nonEmpty) None else Some(value)
        }
      case Vector() =>
        // When the desired nested attribute value is an object itself
        Some(json)
    }

    extract(input, keyVector)
