package edu.self.processor

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpMethod, HttpRequest, HttpResponse}

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}

class RequestProcessor()(
  implicit system: ActorSystem,
  ec: ExecutionContext) {


}

object RequestProcessor {
  def apply()(implicit system: ActorSystem, ec: ExecutionContext): RequestProcessor =
    new RequestProcessor()
}
