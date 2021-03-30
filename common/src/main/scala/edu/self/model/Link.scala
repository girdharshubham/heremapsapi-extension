package edu.self.model

case class Link(linkId: String, shape: List[String], speedLimit: Double, location: Option[List[Double]] = None)
