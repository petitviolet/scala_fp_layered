package net.petitviolet.example.controllers

import akka.http.scaladsl.server.Route
import net.petitviolet.example.domains.impl.AsyncIO
import net.petitviolet.example.infra.daos.Database
import net.petitviolet.example.queries.{ GetAllUserQuery, GetAllUserResult }

import scala.util.{ Failure, Success }

object GetAllUserController extends Controller {

  override protected def parallelism: Int = 2

  private implicit lazy val allFormat = jsonFormat1(GetAllUserResult.apply)

  override lazy val route: Route =
    (get & path("users")) {
      val f = Database.SampleDB.withReadAsync { s =>
        new GetAllUserQuery[AsyncIO]
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
