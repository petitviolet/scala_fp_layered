package net.petitviolet.example

import akka.http.scaladsl.server.{ Route, RouteConcatenation }
import net.petitviolet.example.domains.impl.{ AsyncIO, UserRepositoryImpl }
import net.petitviolet.example.domains.users.UserRepository

import scala.concurrent.ExecutionContext

package object controllers {
  def route: Route = RouteConcatenation.concat(
    GetAllUserController,
    UpdateUserController,
    HealthController
  )

  implicit def futureInstance(implicit ec: ExecutionContext) =
    cats.instances.future.catsStdInstancesForFuture
  implicit val userRepository: UserRepository[AsyncIO] = UserRepositoryImpl

}
