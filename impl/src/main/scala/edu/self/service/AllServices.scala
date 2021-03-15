package edu.self.service

import edu.self.repository.MapsRepository

case class AllServices(mapsRepository: MapsRepository) {
  val routing: RoutingService = RoutingService(mapsRepository)
}
