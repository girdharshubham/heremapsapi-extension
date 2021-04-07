package edu.self.model

case class Link(linkId: String, shape: List[String], speedLimit: Double, location: Option[List[Double]])

object Link {
  def apply(linkId: String, shape: List[String], speedLimit: Double, location: Option[List[Double]] = None): Link =
    new Link(linkId = linkId, shape = shape, speedLimit = speedLimit, location = location)
}