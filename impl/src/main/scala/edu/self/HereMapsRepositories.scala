package edu.self

import akka.actor.ActorSystem
import com.typesafe.config.Config
import edu.self.config.MongoConfig
import edu.self.reposistory.HereMapsRepository
import edu.self.util.Implicits._
import org.mongodb.scala.{MongoClient, MongoDatabase}

import scala.concurrent.ExecutionContext

case class HereMapsRepositories(
                                 route: String,
                                 mapsRepository: HereMapsRepository
                               )

object HereMapsRepositories {
  def apply(config: Config)(implicit
                            system: ActorSystem,
                            ec: ExecutionContext
  ): HereMapsRepositories = {
    val baseUrl: String = config.getString("heremaps.api.routing.url")
    val appCode: String = config.getString("heremaps.app.code")
    val appId: String = config.getString("heremaps.app.id")
    val queryParams: Map[String, AnyRef] = config
      .getObject("heremaps.api.routing.params")
      .map

    val route: String = HereMapsBoot.prepareURL(baseUrl, appCode, appId, queryParams)

    val mongoDbConfig: MongoConfig = MongoConfig(
      proto = config.getString("heremaps.mongodb.proto"),
      username = config.getString("heremaps.mongodb.username"),
      password = config.getString("heremaps.mongodb.password"),
      address = config.getString("heremaps.mongodb.address")
    )

    val mongodbDatabase = MongoClient(mongoDbConfig.toString)
      .getDatabase(config.getString("heremaps.mongodb.address"))

    new HereMapsRepositories(route, new HereMapsRepository(route, mongodbDatabase))
  }
}
