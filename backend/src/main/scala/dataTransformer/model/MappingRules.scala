package dataTransformer.model

import dataTransformer.protocol.DynamicProtocolGenerator

import scala.annotation.targetName


case class MappingRules(exprs: Vector[MappingExpr])


case class MappingExpr(from: String, to: String, mappingType: MappingType)

// Inject logic to execute each mapping type
extension [T](expr: MappingExpr)(using protocolGenerator: DynamicProtocolGenerator[T])
  def executeMapping(input: T): Option[T] =
    expr.mappingType match {
      case MappingType.FieldRename => protocolGenerator.renameAttribute(input, expr.to)
      case MappingType.FieldExtract => protocolGenerator.extractNestedAttributeValue(input, expr.to)
    }

object MappingExpr:
  extension (from: String) 
    @targetName("RenameExpr")
    def ->(to: String): MappingExpr = MappingExpr(from, to, MappingType.FieldRename)
    @targetName("ExtractExpr")
    def ->/(nestedField: String): MappingExpr = MappingExpr(from, nestedField, MappingType.FieldExtract)
  
  

enum MappingType:
  case FieldRename, FieldExtract