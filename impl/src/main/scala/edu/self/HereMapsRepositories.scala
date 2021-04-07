package edu.self

import akka.actor.ActorSystem
import com.typesafe.config.Config
import edu.self.config.MongoConfig
import edu.self.model.{Coordinate, Link}
import edu.self.reposistory.HereMapsRepository
import edu.self.util.Implicits._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

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

    val codecRegistry =
      fromRegistries(fromProviders(classOf[Coordinate], classOf[Link]), DEFAULT_CODEC_REGISTRY)

    val mongodbDatabase = MongoClient(mongoDbConfig.toString)
      .getDatabase(config.getString("heremaps.mongodb.database"))
      .withCodecRegistry(codecRegistry)

    new HereMapsRepositories(route, new HereMapsRepository(route, mongodbDatabase))
  }
}
