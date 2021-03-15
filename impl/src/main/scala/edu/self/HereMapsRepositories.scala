package edu.self

import akka.actor.ActorSystem
import com.typesafe.config.Config
import edu.self.reposistory.HereMapsRepository
import edu.self.util.Implicits._

import scala.concurrent.ExecutionContext

case class HereMapsRepositories(
                                 route: String,
                                 mapsRepository: HereMapsRepository,
                               )

object HereMapsRepositories {
  def apply(config: Config)(
    implicit system: ActorSystem,
    ec: ExecutionContext
  ): HereMapsRepositories = {
    val baseUrl = config.getString("heremaps.api.routing.url")
    val appCode = config.getString("heremaps.app.code")
    val appId = config.getString("heremaps.app.id")
    val queryParams = config
      .getObject("heremaps.api.routing.params")
      .map

    val route = HereMapsBoot.prepareURL(baseUrl, appCode, appId, queryParams)

    new HereMapsRepositories(route, new HereMapsRepository(route))
  }
}
