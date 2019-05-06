package net.petitviolet.example.controllers

import akka.http.scaladsl.server.Route
import cats.data.Validated.{ Invalid, Valid }
import net.petitviolet.example.applications._
import net.petitviolet.example.domains.impl.{ AsyncIO, UserRepositoryImpl }
import net.petitviolet.example.domains.users.UserRepository
import net.petitviolet.example.infra.daos.Database
import spray.json.RootJsonFormat

import scala.util.{ Failure, Success }

object UpdateUserController extends Controller {

  override protected def parallelism: Int = 2

  implicit val format: RootJsonFormat[UpdateUserParam] = jsonFormat2(UpdateUserParam.apply)

  implicit val userRepository: UserRepository[AsyncIO] = UserRepositoryImpl

  override lazy val route: Route =
    (post & path("user" / "update") & entity(as[UpdateUserParam])) { param =>
      val f = Database.SampleDB.localTxAsync { s =>
        new UpdateUserApplication[AsyncIO]
          .execute(param)
          .run((s, executionContext))
      }

      onComplete(f) {
        case Success(Valid(user)) =>
          ok(s"updated: ${user.id}")
        case Success(Invalid(msg)) =>
          logger.warn(s"failed to update user. param = $param, msg = $msg")
          badRequest(msg.toList.mkString(", "))
        case Failure(t) =>
          logger.error(s"failed to update user. param = $param", t)
          serverError(t.getMessage)
      }
    }

}
