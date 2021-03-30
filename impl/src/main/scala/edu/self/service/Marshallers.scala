package edu.self.service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import edu.self.model.{Coordinate, Link, RouteRequest}
import spray.json.{DefaultJsonProtocol, JsObject, JsValue, RootJsonFormat}

trait Marshallers extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val coordinateRequestFormat: RootJsonFormat[Coordinate] = jsonFormat2(Coordinate)
  implicit val routeRequestFormat: RootJsonFormat[RouteRequest] = jsonFormat1(RouteRequest)
  implicit val linkFormat: RootJsonFormat[Link] = new RootJsonFormat[Link] {
    val base: RootJsonFormat[Link] = jsonFormat(
      (linkId: String, shape: List[String], speedLimit: Double, coordinate: Option[List[Double]]) =>
        Link(linkId, shape, speedLimit, coordinate),
      "linkId", "shape", "speedLimit", "coordinate"
    )

    override def read(json: JsValue): Link = base.read(json)

    override def write(obj: Link): JsValue =
      JsObject(base.write(obj).asJsObject.fields)
  }
}
