package edu.self.api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import scalaz.Scalaz._
import scalaz._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import edu.self.service.AllServices

import scala.concurrent.ExecutionContext

case class AllApis(allServices: AllServices)(implicit ec: ExecutionContext) extends UnifiedService

trait UnifiedService {
  def allServices: AllServices

  implicit val routeMonoid: Monoid[Route] = Monoid.instance(_ ~ _, reject)

  val unsecured: Route = List[Route](
      new RoutingApi(allServices.routing).route
  ).concatenate

  def api(unsecured: Route): Route = unsecured
  val route = {
    pathSingleSlash {
      Directives.get {
        complete("Hi! This is the heremaps-api extension")
      }
    } ~ api(unsecured)
  }

}
