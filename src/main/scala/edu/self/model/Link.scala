package edu.self.model

import play.api.libs.json.{Format, Json, OFormat}
import spray.json.DefaultJsonProtocol._

case class Link(linkId: String, shape: List[String], speedLimit: Double)

object Link {
  implicit val linkFormat: Format[Link] = Json.format[Link]
}
