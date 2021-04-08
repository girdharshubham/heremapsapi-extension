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
package edu.self.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import edu.self.service.AllServices
import scalaz.Scalaz._
import scalaz._

import scala.concurrent.ExecutionContext

case class AllApis(allServices: AllServices) extends UnifiedService

trait UnifiedService {
  def allServices: AllServices

  implicit val routeMonoid: Monoid[Route] = Monoid.instance(_ ~ _, reject)

  val unsecured: Route = List[Route](
    new RoutingApi(allServices.routing).route
  ).concatenate

  def api(unsecured: Route): Route = unsecured

  val route: Route = {
    pathSingleSlash {
      Directives.get {
        complete("Hi! This is the heremaps-api extension")
      }
    } ~ api(unsecured)
  }

}
