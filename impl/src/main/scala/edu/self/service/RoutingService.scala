package edu.self.service

import edu.self.model.{Link, RouteRequest}
import edu.self.repository.MapsRepository

import scala.concurrent.Future

class RoutingService(mapsRepository: MapsRepository) {
  def getLinks(routeRequest: RouteRequest): Future[Seq[Link]] = mapsRepository
    .getLinks(routeRequest.start, routeRequest.end)
}

object RoutingService {

  def apply(mapsRepository: MapsRepository): RoutingService =
    new RoutingService(mapsRepository)
}
