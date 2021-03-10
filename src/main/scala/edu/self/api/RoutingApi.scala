package edu.self.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods}
import akka.http.scaladsl.server.Route
import edu.self.service.{Marshallers, RoutingService}
import akka.http.scaladsl.server.Directives._
import edu.self.service.RoutingService.RouteRequest
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.directives

import scala.concurrent.{ExecutionContext, Future}

class RoutingApi(routingService: RoutingService) extends Marshallers {

  def route: Route = {
    pathPrefix("route") {
        get {
          entity(as[RouteRequest]) { request=>
            complete {
              routingService.getLinks(request)
            }
          }
        }
    }
  }

}
