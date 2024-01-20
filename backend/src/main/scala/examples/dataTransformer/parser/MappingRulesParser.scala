package dataTransformer.parser

import dataTransformer.model.{MappingExpr, MappingRules, MappingType}
import dataTransformer.model.MappingExpr.->/
import dataTransformer.model.MappingExpr.->
import scala.util.parsing.combinator.*


object MappingRulesParser extends RegexParsers:
  private def field: Parser[String] = "\"" ~> "[^\".]*".r <~ "\""
  private def nestedField: Parser[String] = "\"" ~> "[^\"]*".r <~ "\""


  private def fieldRename: Parser[MappingExpr] = field ~ "->" ~ field ^^ {
    case from ~ "->" ~ to => from -> to //MappingExpr(from, to, MappingType.FieldRename)
  }

  private def fieldExtract: Parser[MappingExpr] = nestedField ~ "->" ~ repsep(nestedField, ".") ^^ {
    case from ~ "->" ~ to => from ->/ to.mkString(".")  //MappingExpr(from, to.mkString("."), MappingType.FieldExtract)
  }

  //private def fieldMappingExpr: Parser[MappingExpr] = 
  private def mappingExpr: Parser[MappingExpr] = fieldRename | fieldExtract // Renames need to be mapped first as the fieldRename regex matches a subset of the fieldExtract regex
  private def mappingRule: Parser[MappingRules] = opt("{") ~> repsep(mappingExpr, ",") <~ opt(",") <~ opt("}") ^^ {
    rules => MappingRules(rules.toVector)
  }

  def parseMappingRules(input: String): Either[String, MappingRules] = 
    parseAll(mappingRule, input) match {
      case Success(result, _) => Right(result)
      case NoSuccess(msg, next) => Left(s"Failed to parse mapping rules: $msg at line ${next.pos.line}, column ${next.pos.column}")
    }

