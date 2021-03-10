package edu.self

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import com.typesafe.config.ConfigFactory
import edu.self.util.Implicits._

import scala.concurrent.ExecutionContextExecutor

object Main {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("here-maps")
    implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

    val config = ConfigFactory.load()
    val baseUrl = config.getString("heremaps.api.routing.url")
    val appCode = config.getString("heremaps.app.code")
    val appId = config.getString("heremaps.app.id")
    val queryParams = config
      .getObject("heremaps.api.routing.params")
      .map

    val route = HereMapsBoot.prepareURL(baseUrl, appCode, appId, queryParams)
    val repos = HereMapsRepository()
    val request: HttpRequest = repos.requestProcessor.prepareRequests(HttpMethods.GET, route)

    import play.api.libs.json._

    repos
      .requestProcessor
      .processRequest(request)
      .map(entity => Json.parse(entity.data.utf8String))
      .map(_.toSeq)
      .foreach(println)

  }
}
