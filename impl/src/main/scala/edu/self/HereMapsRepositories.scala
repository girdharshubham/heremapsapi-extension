// Copyright (C) 2021-2022 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package edu.self

import akka.actor.ActorSystem
import com.typesafe.config.Config
import edu.self.codec.UTCZonedDateTimeCodec
import edu.self.config.MongoConfig
import edu.self.model.{Coordinate, Link}
import edu.self.reposistory.HereMapsRepository
import edu.self.util.Implicits._
import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecRegistries.{fromCodecs, fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

import java.time.ZonedDateTime
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

    val ttl = config.getInt("heremaps.cache.ttl")

    val codecRegistry =
      fromRegistries(
        fromCodecs(UTCZonedDateTimeCodec),
        fromProviders(classOf[Coordinate], classOf[Link]),
        DEFAULT_CODEC_REGISTRY
      )

    val mongodbDatabase = MongoClient(mongoDbConfig.toString)
      .getDatabase(config.getString("heremaps.mongodb.database"))
      .withCodecRegistry(codecRegistry)

    new HereMapsRepositories(route, new HereMapsRepository(route, mongodbDatabase, ttl))
  }
}
