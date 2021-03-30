package edu.self.reposistory

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import com.mongodb.{BasicDBList, BasicDBObject}
import edu.self.model.{Coordinate, Link}
import edu.self.repository.MapsRepository
import edu.self.util.Implicits._
import org.mongodb.scala.{Document, MongoDatabase}
import spray.json._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class HereMapsRepository(route: String, database: MongoDatabase)(
  implicit system: ActorSystem,
  ec: ExecutionContext
) extends MapsRepository {

  private val collection = database.getCollection("links")

  private def getFromDB(start: Coordinate): Future[Seq[Link]] = {
    val query = new BasicDBObject()
    val loc = new BasicDBObject()
    val near = new BasicDBList()

    near.put("0", start.longitude)
    near.put("1", start.latitude)

    loc.put("$near", near)
    loc.put("$maxDistance", Int.box(0))
    query.put("location", loc)

    collection
      .find(query)
      .toFuture()
      .map(_
        .map(_.toLink))
  }

  private def insert(res: Link): Future[Done] = {
    collection.insertOne(
      Document(
        s"""
           |{
           |        "location": [${res.location.fold("")(_.mkString(","))}],
           |        "shape": []
           |        "linkId": "${res.linkId}",
           |        "speedLimit": ${res.speedLimit}
           |}""".stripMargin)).toFuture().map(_ => Done.getInstance())
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
      val res = getFromApi(start, end)
      res.map {
        _.map { link =>
          insert(link)
        }
      }
      res
    } else {
      Future.successful(links)
    }
  }
}
