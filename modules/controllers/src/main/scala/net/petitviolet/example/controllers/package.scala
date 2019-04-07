package net.petitviolet.example

import akka.http.scaladsl.server.{Route, RouteConcatenation}
import cats.Monad
import net.petitviolet.example.domains.impl.{AsyncIO, UserRepositoryImpl}
import net.petitviolet.example.domains.users.UserRepository
import wvlet.airframe.Design

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

  def newDesign[F[_]: Monad: UserRepository]() =
    wvlet.airframe.newDesign
      .bind[UserRepository[F]]
      .toInstance(implicitly)
      .bind[Monad[F]]
      .toInstance(implicitly)

  def design(implicit ec: ExecutionContext): Design = newDesign[AsyncIO]()
}
