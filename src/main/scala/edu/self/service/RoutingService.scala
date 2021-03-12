package edu.self.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import edu.self.model.Link
import edu.self.service.RoutingService.RouteRequest
import play.api.libs.json.Json

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class RoutingService(route: String)(
  implicit system: ActorSystem,
  ec: ExecutionContext) {
  def getLinks(routeRequest: RouteRequest): Future[Seq[Link]] = {
    val request = HttpRequest(method = HttpMethods.GET, uri =
      s"${route}&waypoint0=${routeRequest.start.latitude},${routeRequest.start.longitude}&waypoint1=${routeRequest.end.latitude},${routeRequest.end.longitude}")

    import edu.self.util.Implicits._
    Http()
      .singleRequest(request)
      .flatMap(_.entity.toStrict(2 seconds))
      .map(entity => Json.parse(entity.data.utf8String))
      .map(_.toSeq)
  }
}

object RoutingService {
  final case class Coordinate(latitude: Double, longitude: Double)
  final case class RouteRequest(start: Coordinate, end: Coordinate)

  def apply(route: String)(implicit system: ActorSystem, ec: ExecutionContext): RoutingService =
    new RoutingService(route)
}
