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

import scala.annotation.tailrec

object HereMapsBoot {
  @tailrec
  def break(appCode: String, appId: String, queryParams: Map[String, AnyRef], resString: String): String =
    queryParams.keys.toList match {
      case key :: Nil => s"${resString}&app_id=${appId}&app_code=${appCode}"
      case key :: _ =>
        break(
          appCode,
          appId,
          queryParams - key,
          s"${resString}&${key}=${queryParams(key)}"
        )
    }

  def prepareURL(baseUrl: String, appCode: String, appId: String, queryParams: Map[String, AnyRef]): String =
    baseUrl + "?" + break(appCode, appId, queryParams, "")

}
