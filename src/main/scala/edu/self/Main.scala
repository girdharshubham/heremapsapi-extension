package edu.self

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import edu.self.api.AllApis
import edu.self.service.AllServices
import edu.self.util.Implicits._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main extends LazyLogging {

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

    val server = Http()
      .newServerAt("0.0.0.0", 9000)
      .bindFlow(AllApis(AllServices(route)).route)

    logger.info("Server started at 0.0.0.0, press <ENTER> to trigger a shutdown")
    StdIn.readLine()
    logger.info(  "Shutdown triggered")
    server
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
