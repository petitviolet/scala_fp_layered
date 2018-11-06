package net.petitviolet.example

import net.petitviolet.example.domains.impl.{AsyncIO, UserRepositoryImpl}
import net.petitviolet.example.domains.users.UserRepository
import scalaz.Monad

import scala.concurrent.ExecutionContext

package object controllers {
  implicit val kleisliMonadReader = scalaz.Kleisli.kleisliMonadReader
  implicit def futureInstance(implicit ec: ExecutionContext) =
    scalaz.std.scalaFuture.futureInstance
  implicit val userRepository: UserRepository[AsyncIO] = UserRepositoryImpl

  def newDesign[F[_]: Monad: UserRepository]() =
    wvlet.airframe.newDesign
      .bind[UserRepository[F]]
      .toInstance(implicitly)
      .bind[Monad[F]]
      .toInstance(implicitly)

}
