package dataTransformer.model

import dataTransformer.protocol.DynamicProtocolGenerator
import spray.json.JsValue

import scala.annotation.targetName


case class MappingRules(exprs: Vector[MappingExpr])


case class MappingExpr(from: String, to: String, mappingType: MappingType, arrayMapping: Option[(String, Vector[MappingExpr])] = None)

// Inject logic to execute each mapping type
extension (expr: MappingExpr)
  def executeMapping[T](input: T)(using protocolGenerator: DynamicProtocolGenerator[T]): Option[JsValue] =
    expr.mappingType match {
      case MappingType.FieldRename => protocolGenerator.renameAttribute(input, expr.from)
      case MappingType.FieldExtract => protocolGenerator.extractNestedAttributeValue(input, expr.from)
      case MappingType.ArrayMapping => protocolGenerator.applyToEachElement(input, MappingType.ArrayMapping, expr.arrayMapping.get._1, expr.arrayMapping.get._2)
    }

object MappingExpr:
  extension (from: String) 
    @targetName("RenameExpr")
    def ->(to: String): MappingExpr = MappingExpr(from, to, MappingType.FieldRename)
    @targetName("ExtractExpr")
    def ->/(nestedField: String): MappingExpr = MappingExpr(from, nestedField, MappingType.FieldExtract)
    
    @targetName("ArrayMapExpr")
    def ->*(expressions: Vector[MappingExpr]): MappingExpr = MappingExpr(from, "", MappingType.ArrayMapping, Some((from, expressions)))



enum MappingType:
  case FieldRename, FieldExtract, ArrayMapping