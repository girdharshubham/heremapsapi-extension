package edu.self.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import edu.self.service.AllServices
import scalaz.Scalaz._
import scalaz._

import scala.concurrent.ExecutionContext

case class AllApis(allServices: AllServices) extends UnifiedService

trait UnifiedService {
  def allServices: AllServices

  implicit val routeMonoid: Monoid[Route] = Monoid.instance(_ ~ _, reject)

  val unsecured: Route = List[Route](
    new RoutingApi(allServices.routing).route
  ).concatenate

  def api(unsecured: Route): Route = unsecured

  val route: Route = {
    pathSingleSlash {
      Directives.get {
        complete("Hi! This is the heremaps-api extension")
      }
    } ~ api(unsecured)
  }

}
