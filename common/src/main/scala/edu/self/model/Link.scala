package edu.self.model

import play.api.libs.json.{Format, Json}

case class Link(linkId: String, shape: List[String], speedLimit: Double, coordinate: Option[List[Double]] = None)

object Link {
  implicit val coordinateFormat: Format[Coordinate] = Json.format[Coordinate]
  implicit val linkFormat: Format[Link] = Json.format[Link]
}
