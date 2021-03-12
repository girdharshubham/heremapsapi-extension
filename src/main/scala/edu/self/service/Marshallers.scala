package edu.self.service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import edu.self.model.Link
import edu.self.service.RoutingService.{Coordinate, RouteRequest}
import spray.json.{DefaultJsonProtocol, JsObject, JsValue, RootJsonFormat}

trait Marshallers extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val coordinateRequestFormat: RootJsonFormat[Coordinate] = jsonFormat2(Coordinate)
  implicit val routeRequestFormat: RootJsonFormat[RouteRequest] = jsonFormat2(RouteRequest)
  implicit val linkFormat: RootJsonFormat[Link] = new RootJsonFormat[Link] {
    val base = jsonFormat(
      (linkId: String, shape: List[String], speedLimit: Double) =>
        Link(linkId, shape, speedLimit), "linkId", "shape", "speedLimit"
    )

    override def read(json: JsValue): Link = base.read(json)

    override def write(obj: Link): JsValue =
      JsObject(base.write(obj).asJsObject.fields)
  }
}
