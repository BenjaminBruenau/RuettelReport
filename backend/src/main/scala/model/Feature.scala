package model

case class Feature(
  `type`: String,
  properties: Properties,
  geometry: Geometry,
  id: String
)
