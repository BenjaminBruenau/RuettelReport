package dataTransformer.util

import spray.json.{JsObject, JsValue}

object JsonUtils {
  def constructNestedObject(keys: Vector[String], value: JsValue): (String, JsValue) =
    keys.head -> keys.tail.foldRight(value: JsValue)((key, acc) => JsObject(key -> acc))

  def mergeObjects(obj1: JsObject, obj2: JsObject): JsObject =
    JsObject(obj1.fields ++ obj2.fields)
}
