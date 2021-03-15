package edu.self.model

import play.api.libs.json.{Format, Json}

case class Link(linkId: String, shape: List[String], speedLimit: Double)

object Link {
  implicit val linkFormat: Format[Link] = Json.format[Link]
}
