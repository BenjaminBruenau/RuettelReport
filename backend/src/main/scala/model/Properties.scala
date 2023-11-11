package model

case class Properties(
  mag: Double,
  place: Option[String],
  time: Long,
  updated: Long,
  tz: Option[String],
  url: String,
  detail: String,
  felt: Option[Int],
  cdi: Option[Double],
  mmi: Option[Double],
  alert: Option[String],
  status: String,
  tsunami: Int,
  sig: Int,
  net: String,
  code: String,
  ids: String,
  sources: String,
  types: String,
  nst: Option[Int],
  dmin: Option[Double],
  rms: Double,
  gap: Option[Double],
  magType: String,
  `type`: String,
  title: String
)
