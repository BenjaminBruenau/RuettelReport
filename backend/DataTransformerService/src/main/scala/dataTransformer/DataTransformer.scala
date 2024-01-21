package dataTransformer

import spray.json.{JsValue, JsonFormat}


object DataTransformer:
  def transform[T](input: T, protocol: JsonFormat[T]): JsValue = 
    // input could be any data format, e.g. xml or json
    protocol.write(input)
  
