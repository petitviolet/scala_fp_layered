package net.petitviolet.example.controllers

import akka.http.scaladsl.server.Route
import net.petitviolet.example.applications._
import net.petitviolet.example.domains.impl.AsyncIO
import net.petitviolet.example.infra.orm.Database
import spray.json.RootJsonFormat

import scala.util.{Failure, Success}

object UpdateUserController extends Controller {

  override protected def parallelism: Int = 2

  implicit val format: RootJsonFormat[UpdateUserParam] = jsonFormat2(
    UpdateUserParam.apply)

  lazy val app = design.withSession { session =>
    session
      .build[UpdateUserApplication[AsyncIO]]
  }

  override lazy val route: Route =
    (post & path("user" / "update") & entity(as[UpdateUserParam])) { param =>
      val f = Database.SampleDB.localTxAsync { s =>
        app
          .execute(param)
          .run((s, executionContext))
      }

      onComplete(f) {
        case Success(Right(UpdateUserResult(id))) =>
          ok(s"updated: $id")
        case Success(Left(msg)) =>
          logger.warn(s"failed to update user. param = $param, msg = $msg")
          badRequest(msg)
        case Failure(t) =>
          logger.error(s"failed to update user. param = $param", t)
          serverError(t.getMessage)
      }
    }

}
