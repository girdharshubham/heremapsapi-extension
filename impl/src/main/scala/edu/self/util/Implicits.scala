package edu.self.util

import com.typesafe.config.ConfigObject
import play.api.libs.json.JsValue

object Implicits {

  implicit class ConfigToMap(val configObject: ConfigObject) extends AnyVal {

    import collection.JavaConverters._

    def map: Map[String, AnyRef] = configObject
      .unwrapped()
      .asScala
      .toMap
  }

  implicit class JsValueToVectorLink(val jsValue: JsValue) extends AnyVal {

    import edu.self.model.Link

    def toSeq: Seq[Link] =
      (((jsValue \ "response" \ "route") (0) \ "leg") (0) \ "link").as[Seq[Link]]
        .map(link => link.copy(linkId = link.linkId.replaceAll("\\+|\\-", "")))
  }

}
