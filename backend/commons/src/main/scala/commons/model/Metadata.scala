package commons.model

case class Metadata(
  generated: Long, 
  url: String,
  title: String,
  status: Int,
  api: String,
  count: Int
)
