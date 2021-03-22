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
