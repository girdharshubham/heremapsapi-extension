package edu.self.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import edu.self.model.{Link, RouteRequest}
import edu.self.repository.MapsRepository
import play.api.libs.json.Json
import edu.self.util.Implicits._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class RoutingService(mapsRepository: MapsRepository){
  def getLinks(routeRequest: RouteRequest): Future[Seq[Link]] = mapsRepository
    .getLinks(routeRequest.start, routeRequest.end)
}

object RoutingService {

  def apply(mapsRepository: MapsRepository): RoutingService =
    new RoutingService(mapsRepository)
}
