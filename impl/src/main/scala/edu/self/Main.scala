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
package edu.self

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import edu.self.api.AllApis
import edu.self.service.AllServices

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main extends LazyLogging {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("here-maps")
    implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
    val config: Config = ConfigFactory.load()

    val repos: HereMapsRepositories = HereMapsRepositories(config)

    val server = Http()
      .newServerAt("0.0.0.0", 9000)
      .bindFlow(AllApis(AllServices(repos.mapsRepository)).route)

    logger.info("Server started at 0.0.0.0, press <ENTER> to trigger a shutdown")
    StdIn.readLine()
    logger.info("Shutdown triggered")
    server
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
