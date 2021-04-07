package edu.self.reposistory

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import edu.self.model.{Coordinate, Link}
import edu.self.repository.MapsRepository
import edu.self.util.Implicits._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.geojson._
import org.mongodb.scala.{MongoCollection, MongoDatabase, _}
import spray.json._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class HereMapsRepository(route: String, database: MongoDatabase)(
  implicit system: ActorSystem,
  ec: ExecutionContext
) extends MapsRepository {

  private val collection: MongoCollection[Link] = database.getCollection("links")

  private def getFromDB(start: Coordinate): Future[Seq[Link]] = {
    val point = Point(Position(start.longitude, start.latitude))
    collection
      .find(Filters.near("location", point, Some(0.0), None))
      .toFuture()
  }

  private def insert(link: Link): Future[Done] = {
    collection
      .insertOne(link)
      .toFuture()
      .map(_ => Done.getInstance())
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

  def getLinks(start: Coordinate, end: Coordinate): Future[Seq[Link]] = getFromDB(start).flatMap { links =>
    if (links.isEmpty) {
      val res: Future[Seq[Link]] = getFromApi(start, end)
      res.map(_.map(link => insert(link)))
      res
    } else {
      Future.successful(links)
    }
  }
}
