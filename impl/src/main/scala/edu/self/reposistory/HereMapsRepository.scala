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
package edu.self.reposistory

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import com.typesafe.scalalogging.LazyLogging
import edu.self.model.{Coordinate, Link}
import edu.self.repository.MapsRepository
import edu.self.util.Implicits._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.geojson._
import org.mongodb.scala.{MongoCollection, MongoDatabase, _}
import spray.json._

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class HereMapsRepository(route: String, database: MongoDatabase, override val timeToLive: Int)(
  implicit system: ActorSystem,
  ec: ExecutionContext
) extends MapsRepository with LazyLogging {

  override val collection: MongoCollection[Link] = database.getCollection("links")

  override def insert(link: Link): Future[Done] = {
    collection
      .insertOne(link)
      .toFuture()
      .map(_ => Done.getInstance())
  }

  override def update(link: Link): Future[Done] = {
    collection
      .replaceOne(Filters.equal("linkId", s"${link.linkId}"), link)
      .toFuture().map(_ => Done.getInstance())
  }

  override def getLinks(start: Coordinate, end: Coordinate): Future[Seq[Link]] = getFromDB(start).flatMap {
    case links if links.isEmpty =>
      val res = getFromApi(start, end)
      res.map(_.map(link => insert(link)))
      res
    case links if links.nonEmpty =>
      links.map { link =>
        if (ChronoUnit.MINUTES.between(link.updatedAt.get, ZonedDateTime.now()) > timeToLive) {
          logger.info("TTL Expired. Updating")
          val res = getFromApi(start, end)
          res.map(_.map(link => update(link)))
          res
        }
      }
      Future.successful(links)
  }

  private def getFromDB(start: Coordinate): Future[Seq[Link]] = {
    val point = Point(Position(start.longitude, start.latitude))
    collection
      .find(Filters.near("location", point, Some(0.0), None))
      .toFuture()
  }

  private def getFromApi(start: Coordinate, end: Coordinate): Future[Seq[Link]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"${route}&waypoint0=${start.latitude},${start.longitude}&waypoint1=${end.latitude},${end.longitude}")

    Http()
      .singleRequest(request)
      .flatMap(_.entity.toStrict(2 seconds))
      .map(entity => entity.data.utf8String.parseJson)
      .map(_.toSeq(start))
  }
}
