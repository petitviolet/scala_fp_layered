package net.petitviolet.example

import net.petitviolet.example.domains.impl.{AsyncIO, UserRepositoryImpl}
import net.petitviolet.example.domains.users.UserRepository
import scalaz.Monad

import scala.concurrent.ExecutionContext

package object controllers {

  import scalaz.Kleisli._
  import scalaz.std.scalaFuture.futureInstance

  def newDesign()(implicit ec: ExecutionContext) =
    wvlet.airframe.newDesign
      .bind[UserRepository[AsyncIO]]
      .toInstance(UserRepositoryImpl)
      .bind[Monad[AsyncIO]]
      .toInstance(implicitly[Monad[AsyncIO]])

}
