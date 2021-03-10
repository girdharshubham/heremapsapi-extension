package edu.self

import akka.actor.ActorSystem
import edu.self.processor.RequestProcessor

import scala.concurrent.ExecutionContext

case class HereMapsRepository(
                               requestProcessor: RequestProcessor
                             )

object HereMapsRepository {
  def apply()
           (implicit system: ActorSystem, ec: ExecutionContext): HereMapsRepository = {
    HereMapsRepository(RequestProcessor())
  }
}
