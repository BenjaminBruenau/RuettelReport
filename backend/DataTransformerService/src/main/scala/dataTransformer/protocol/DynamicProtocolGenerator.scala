package dataTransformer.protocol

import dataTransformer.model.MappingType.{FieldExtract, FieldRename}
import dataTransformer.model.{MappingExpr, MappingRules, MappingType, executeMapping}
import spray.json.{JsArray, JsNull, JsObject, JsString, JsValue, JsonFormat}

import scala.xml.{Elem, Node}

trait DynamicProtocolGenerator[T]:
  def generateProtocol(mappingRules: MappingRules): JsonFormat[T]

  def renameAttribute(input: T, attribute: String): Option[JsValue]
  def extractNestedAttributeValue(input: T, path: String): Option[JsValue]

  def applyToEachElement(input: T, mappingType: MappingType, arrayField: String, expressions: Vector[MappingExpr]): Option[JsValue]

given DynamicProtocolGenerator[JsValue] with

  def generateProtocol(mappingRules: MappingRules): JsonFormat[JsValue] = new JsonFormat[JsValue] {
    def read(json: JsValue): JsValue = throw new UnsupportedOperationException("Reading not supported")

    def write(input: JsValue): JsValue =

      val transformedFields = for {
        rule <- mappingRules.exprs
        value <- rule.executeMapping[JsValue](input)
      } yield if rule.to.isEmpty then rule.arrayMapping.get._1 -> value else  constructNestedObject(rule.to.split("\\.").toVector, value)

      JsObject(transformedFields.foldJsonStructures)
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

  def applyForEach(input: JsValue, from: String, to: String, mappingType: MappingType, arrayField: String): Option[(String, JsValue)] =
    def traverseAndApply(json: JsValue, pathSegments: List[String]): Option[JsValue] =
      pathSegments match {
        case head :: tail =>
          json match {
            case JsObject(fields) =>
              // move down the path until array to map is found
              fields.get(head).flatMap(subJson => traverseAndApply(subJson, tail))
            case JsArray(elements) =>
              val path = if mappingType == MappingType.FieldRename then head else from.substring(from.lastIndexOf(head))
              Some(JsArray(elements.flatMap(element => applyMapping(element, path, to, mappingType))))
            case _ => None // value found instead of an array or nested object
          }
        case Nil =>
          applyMapping(json, from, to, mappingType) // array is the input
      }

    val pathSegments = from.split("\\.").toList
    Some(arrayField -> traverseAndApply(input, pathSegments).get)


  def applyToEachElement(input: JsValue, mappingType: MappingType, arrayField: String, expressions: Vector[MappingExpr]): Option[JsValue] =

    val result = for {
      rule <- expressions
      value <- applyForEach(input, rule.from, rule.to, rule.mappingType, arrayField)
    } yield value

    val mergedJsArray = result.foldJsonStructures
    mergedJsArray.get(arrayField) match
      case Some(value) => Some(value)
      case None => None


  private def applyMapping(input: JsValue, path: String, to: String, mappingType: MappingType): Option[JsValue] =
    mappingType match {
      case FieldRename =>
        renameAttribute(input, path).map(obj => JsObject(constructNestedObject(to.split("\\.").toVector, obj)))
      case FieldExtract =>
        extractNestedAttributeValue(input, path).map(obj => JsObject(constructNestedObject(to.split("\\.").toVector, obj)))
      case _ => Some(input)
    }



given DynamicProtocolGenerator[Elem] with
  def generateProtocol(mappingRules: MappingRules): JsonFormat[Elem] = new JsonFormat[Elem] {
    def read(json: JsValue): Elem = throw new UnsupportedOperationException("Reading not supported")

    def write(input: Elem): JsValue =
      val xmlAsJson = xmlToJson(input)
      val transformedFields = for {
        rule <- mappingRules.exprs
        value <- rule.executeMapping[JsValue](xmlAsJson)
      } yield if rule.to.isEmpty then rule.arrayMapping.get._1 -> value else constructNestedObject(rule.to.split("\\.").toVector, value)
      
      JsObject(transformedFields.foldJsonStructures)
  }

  def xmlToJson(xml: Elem): JsValue =
    def convertNode(node: Node): JsValue = node match {
      case e: Elem =>
        // Text between tags
        val pcdata = e.child.filter(_.isInstanceOf[scala.xml.Text])
        // Attributes inside the tag
        val attributes = e.attributes.asAttrMap.map { case (k, v) => (s"@$k", JsString(v)) }
        val tagData = if pcdata.nonEmpty then attributes ++ Map((pcdata.head.label, convertNode(pcdata.head))) else attributes

        val children = e.child.filterNot(_.isInstanceOf[scala.xml.Text]).groupBy(_.label)


        if children.nonEmpty then
          val childrenGroupsByLabel = children.map { case (label, group) =>
            (label, if group.length > 1 then JsArray(group.map(convertNode).toVector) else convertNode(group.head))
          }
          JsObject(tagData ++ childrenGroupsByLabel)
        else
          JsObject(tagData)
      case scala.xml.PCData(data) => JsString(data.trim.stripLineEnd)
      case scala.xml.Text(text) => JsString(text.trim.stripLineEnd)
      case _ => JsNull
    }

    val children = xml.child.filterNot(_.isInstanceOf[scala.xml.Text]).groupBy(_.label)
    val groups = children.map { case (label, group) =>
      (label, if group.length > 1 then JsArray(group.map(convertNode).toVector) else convertNode(group.head))
    }
    JsObject(groups)


  def renameAttribute(input: Elem, attribute: String): Option[JsValue] = ???

  def extractNestedAttributeValue(input: Elem, path: String): Option[JsValue] = ???

  def applyToEachElement(input: Elem, mappingType: MappingType, arrayField: String, expressions: Vector[MappingExpr]): Option[JsValue] = ???



// JSON UTILS

def constructNestedObject(keys: Vector[String], value: JsValue): (String, JsValue) =
  keys.head -> keys.tail.foldRight(value: JsValue)((key, acc) => JsObject(key -> acc))

def mergeObjects(obj1: JsObject, obj2: JsObject): JsObject =
  val mergedFields = obj1.fields ++ obj2.fields.map {
    case (key, value) =>
      key -> (obj1.fields.get(key) match {
        // merge nested Objects
        case Some(existingValue: JsObject) if value.isInstanceOf[JsObject] =>
          mergeObjects(existingValue, value.asInstanceOf[JsObject])
        case _ =>
          value
      })
  }

  JsObject(mergedFields)


extension (structures: Vector[(String, JsValue)])
  // this will merge structures containing the same key into a single structure
  // fold left for ordering
  def foldJsonStructures: Map[String, JsValue] =  structures.foldLeft(Map.empty[String, JsValue]) { (acc, field) =>
    field match {
      case (key, value: JsArray) if acc.contains(key) =>
        acc + (key -> JsArray(value.elements.zip(acc(key).asInstanceOf[JsArray].elements).map { case (elem1, elem2) => mergeObjects(elem1.asJsObject, elem2.asJsObject) }))
      // parent key already present which means the nested fields of the object need to be merged
      case (key, value: JsObject) if acc.contains(key) => acc + (key -> mergeObjects(acc(key).asJsObject, value))
      case (key, value) => acc + (key -> value)
    }
  }

