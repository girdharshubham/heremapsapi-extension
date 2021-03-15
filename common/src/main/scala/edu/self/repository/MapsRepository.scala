package edu.self.repository

import edu.self.model.{Coordinate, Link}

import scala.concurrent.Future

trait MapsRepository {
  def getLinks(start: Coordinate, end: Coordinate): Future[Seq[Link]]
}
