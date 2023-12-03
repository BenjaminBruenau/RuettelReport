package dataTransformer

import spray.json.{JsObject, JsValue, JsonFormat}


object DataTransformer:
  def transform[T](input: T, protocol: JsonFormat[T]): JsValue = 
    // input could be either xml or json
    protocol.write(input)
  
