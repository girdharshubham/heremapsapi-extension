package edu.self.service

import akka.actor.ActorSystem
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext

case class AllServices(route: String)(
  implicit system: ActorSystem,
  ec: ExecutionContext) {
  val routing: RoutingService = RoutingService(route)
}
