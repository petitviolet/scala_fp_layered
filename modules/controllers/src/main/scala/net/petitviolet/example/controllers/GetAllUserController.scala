package net.petitviolet.example.controllers

import akka.http.scaladsl.server.Route
import net.petitviolet.example.applications.{
  GetAllUserApplication,
  GetAllUserResult,
  UserResult
}
import net.petitviolet.example.domains.impl.{AsyncIO, UserRepositoryImpl}
import net.petitviolet.example.domains.users.UserRepository
import net.petitviolet.example.infra.orm.Database

import scala.util.{Failure, Success}

object GetAllUserController extends Controller {
  import scalaz.std.scalaFuture.futureInstance

  override protected def parallelism: Int = 2

  implicit val userRepository: UserRepository[AsyncIO] = UserRepositoryImpl
  private implicit lazy val eachFormat = jsonFormat3(UserResult.apply)
  private implicit lazy val allFormat = jsonFormat1(GetAllUserResult.apply)

  override lazy val route: Route =
    (get & path("users")) {
      val f = Database.SampleDB.withReadAsync { s =>
        new GetAllUserApplication[AsyncIO]
          .execute()
          .run((s, executionContext))
      }

      onComplete(f) {
        case Success(results) =>
          ok(results)
        case Failure(t) =>
          logger.error(s"failed to get all user.", t)
          serverError(t.getMessage)
      }
    }

}
