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
package edu.self.service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import edu.self.model.{Coordinate, Link, RouteRequest}
import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonFormat, deserializationError}

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}

trait Marshallers extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val coordinateRequestFormat: RootJsonFormat[Coordinate] = jsonFormat2(Coordinate)
  implicit val routeRequestFormat: RootJsonFormat[RouteRequest] = jsonFormat1(RouteRequest)
  implicit val zonedDateTimeFormat: RootJsonFormat[ZonedDateTime] = new RootJsonFormat[ZonedDateTime] {

    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault)

    def write(obj: ZonedDateTime): JsValue = {
      JsString(formatter.format(obj))
    }

    def read(json: JsValue): ZonedDateTime = json match {
      case JsString(s) => try {
        ZonedDateTime.parse(s, formatter)
      } catch {
        case t: Throwable => error(s)
      }
      case _ =>
        error(json.toString())
    }

    def error(v: Any): ZonedDateTime = {
      val example = formatter.format(ZonedDateTime.now())
      deserializationError(f"'$v' is not a valid date value. Dates must be in compact ISO-8601 format, e.g. '$example'")
    }
  }

  implicit val linkFormat: RootJsonFormat[Link] = new RootJsonFormat[Link] {
    val base: RootJsonFormat[Link] = jsonFormat(
      (linkId: String, shape: List[String], speedLimit: Double, location: Option[List[Double]], updatedAt: Option[ZonedDateTime]) =>
        Link(linkId, shape, speedLimit, location, updatedAt),
      "linkId", "shape", "speedLimit", "location", "updatedAt"
    )

    override def read(json: JsValue): Link = base.read(json)

    override def write(obj: Link): JsValue =
      JsObject(base.write(obj).asJsObject.fields)
  }
}
