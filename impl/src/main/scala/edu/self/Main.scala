package edu.self

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import edu.self.api.AllApis
import edu.self.service.AllServices

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main extends LazyLogging {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("here-maps")
    implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
    val config = ConfigFactory.load()

    val repos = HereMapsRepositories(config)

    val server = Http()
      .newServerAt("0.0.0.0", 9000)
      .bindFlow(AllApis(AllServices(repos.mapsRepository)).route)

    logger.info("Server started at 0.0.0.0, press <ENTER> to trigger a shutdown")
    StdIn.readLine()
    logger.info("Shutdown triggered")
    server
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
