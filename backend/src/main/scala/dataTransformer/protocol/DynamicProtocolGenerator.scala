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
      } yield constructNestedObject(rule.from.split("\\.").toVector, value)

      // fold left for ordering
      JsObject(transformedFields.foldLeft(Map.empty[String, JsValue]) { (acc, field) =>
        field match {
          // parent key already present which means the nested fields of the object need to be merged
          case (key, value: JsObject) if acc.contains(key) => acc + (key -> mergeObjects(acc(key).asJsObject, value))
          case (key, value) => acc + (key -> value)
        }
      })
  }

  private def constructNestedObject(keys: Vector[String], value: JsValue) =
    keys.head -> keys.tail.foldRight(value: JsValue)((key, acc) => JsObject(key -> acc))

  private def mergeObjects(obj1: JsObject, obj2: JsObject): JsObject =
    JsObject(obj1.fields ++ obj2.fields)

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
