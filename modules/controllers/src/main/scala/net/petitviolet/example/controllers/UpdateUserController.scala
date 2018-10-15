package net.petitviolet.example.controllers

import akka.http.scaladsl.server.Route
import net.petitviolet.example.applications.{
  UpdateUserApplication,
  UpdateUserParam,
  UpdateUserResult
}
import net.petitviolet.example.domains.impl.{AsyncIO, UserRepositoryImpl}
import net.petitviolet.example.domains.users.UserRepository
import net.petitviolet.example.infra.orm.Database
import spray.json.RootJsonFormat

import scala.util.{Failure, Success}

object UpdateUserController extends Controller {
  import scalaz.std.scalaFuture.futureInstance

  override protected def parallelism: Int = 2

  implicit val userRepository: UserRepository[AsyncIO] = UserRepositoryImpl
  implicit val format: RootJsonFormat[UpdateUserParam] = jsonFormat2(
    UpdateUserParam.apply)

  override lazy val route: Route =
    (post & path("user" / "update") & entity(as[UpdateUserParam])) { param =>
      val f = Database.SampleDB.localTxAsync { s =>
        new UpdateUserApplication[AsyncIO]
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
