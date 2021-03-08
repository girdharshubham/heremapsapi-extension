package edu.self.util

import com.typesafe.config.ConfigObject

object Implicits {

  implicit class ConfigToMap(configObject: ConfigObject) {
    import collection.JavaConverters._
    def map: Map[String, AnyRef] = configObject
      .unwrapped()
      .asScala
      .toMap
  }

}
