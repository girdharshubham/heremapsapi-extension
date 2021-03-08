package edu.self

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import com.typesafe.config.{ConfigFactory, ConfigObject}
import util.Implicits._
import scala.concurrent.duration._

import scala.concurrent.ExecutionContextExecutor

object Main {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()

    val baseUrl = config.getString("heremaps.api.routing.url")
    val appCode = config.getString("heremaps.app.code")
    val appId = config.getString("heremaps.app.id")
    val queryParams = config
      .getObject("heremaps.api.routing.params")
      .map

    val route = HereMapsBoot.prepareURL(baseUrl, appCode, appId, queryParams)
    implicit val system: ActorSystem = ActorSystem("here-maps")
    implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = route
    )

    Http()
      .singleRequest(request)
      .flatMap(_.entity.toStrict(2 seconds))
      .map(_.data.utf8String).foreach(println)
  }
}
