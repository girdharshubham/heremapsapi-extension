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
package edu.self.util

import com.typesafe.config.ConfigObject
import edu.self.model.{Coordinate, Link}
import edu.self.service.Marshallers
import org.mongodb.scala.Document
import spray.json._

import java.time.ZonedDateTime

object Implicits {

  implicit class ConfigToMap(val configObject: ConfigObject) extends AnyVal {

    import collection.JavaConverters._

    def map: Map[String, AnyRef] = configObject
      .unwrapped()
      .asScala
      .toMap
  }

  implicit class JsValueToVectorLink(jsValue: JsValue) extends Marshallers {

    def toSeq(coordinate: Coordinate): Seq[Link] = {

      val fields: Map[String, JsValue] = jsValue.asJsObject.fields
        .flatMap { response =>
          response._2.asJsObject.fields
        }

      val route = fields("route")
        .convertTo[JsArray]
        .elements(0)
        .compactPrint
        .parseJson
        .asJsObject()
        .fields

      val leg = route("leg")
        .convertTo[JsArray]
        .elements(0)
        .compactPrint
        .parseJson
        .asJsObject()
        .fields

      leg("link")
        .convertTo[Seq[Link]]
        .map { link =>
          link.copy(
            linkId = link.linkId.replaceAll("\\+|\\-", ""),
            location = Some(List(coordinate.longitude, coordinate.latitude)),
            updatedAt = Some(ZonedDateTime.now())
          )
        }
    }
  }

  implicit class DocumentToLink(doc: Document) extends Marshallers {
    def toLink: Link =
      doc.toJson().parseJson.convertTo[Link]
  }

}
