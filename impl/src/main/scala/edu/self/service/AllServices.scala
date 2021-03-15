package edu.self.service

import akka.actor.ActorSystem
import edu.self.repository.MapsRepository

import scala.concurrent.ExecutionContext

case class AllServices(mapsRepository: MapsRepository)(
  implicit system: ActorSystem,
  ec: ExecutionContext) {
  val routing: RoutingService = RoutingService(mapsRepository)
}
