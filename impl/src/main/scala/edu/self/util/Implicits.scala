package edu.self.util

import com.typesafe.config.ConfigObject
import edu.self.model.{Coordinate, Link}
import edu.self.service.Marshallers
import org.mongodb.scala.Document
import spray.json._

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

      leg("link").convertTo[Seq[Link]]
        .map { link =>
          link.copy(
            linkId = link.linkId.replaceAll("\\+|\\-", ""),
            location = Some(List(coordinate.longitude, coordinate.latitude
            )))
        }
    }
  }

  implicit class DocumentToLink(doc: Document) extends Marshallers {
    def toLink: Link =
      doc.toJson().parseJson.convertTo[Link]
  }

}
