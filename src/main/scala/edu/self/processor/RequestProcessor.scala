package edu.self.processor

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpMethod, HttpRequest, HttpResponse}

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}

class RequestProcessor()(
  implicit system: ActorSystem,
  ec: ExecutionContext) {
  def prepareRequests(method: HttpMethod, route: String): HttpRequest =
    HttpRequest(method = method, uri = route)

  def processRequest(request: HttpRequest): Future[HttpEntity.Strict] = Http()
    .singleRequest(request)
    .flatMap(_.entity.toStrict(2 seconds))

}

object RequestProcessor {
  def apply()(implicit system: ActorSystem, ec: ExecutionContext): RequestProcessor =
    new RequestProcessor()
}
