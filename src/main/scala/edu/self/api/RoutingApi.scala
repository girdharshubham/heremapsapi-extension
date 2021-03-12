package edu.self.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import edu.self.service.RoutingService.RouteRequest
import edu.self.service.{Marshallers, RoutingService}

class RoutingApi(routingService: RoutingService) extends Marshallers {

  def route: Route = {
    pathPrefix("route") {
      get {
        entity(as[RouteRequest]) { request =>
          complete {
            routingService.getLinks(request)
          }
        }
      }
    }
  }

}
