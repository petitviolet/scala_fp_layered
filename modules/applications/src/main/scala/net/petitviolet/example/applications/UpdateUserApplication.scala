package net.petitviolet.example.applications

import cats.Monad
import cats.syntax.{FlatMapSyntax, FunctorSyntax}
import net.petitviolet.example.domains.Id
import net.petitviolet.example.domains.users.{User, UserRepository}
import wvlet.airframe.bind

trait UpdateUserApplication[F[_]] extends FunctorSyntax with FlatMapSyntax {
  private implicit val userRepository: UserRepository[F] =
    bind[UserRepository[F]]
  private implicit val M: Monad[F] = bind[Monad[F]]

  def execute(param: UpdateUserParam): F[Either[String, UpdateUserResult]] = {
    def pure[A](a: A): F[A] = implicitly[Monad[F]].pure(a)

    val UpdateUserParam(userId, newName) = param
    userRepository.findById(Id(userId)) flatMap {
      case Some(user) =>
        User.Name
          .create(newName)
          .map { user.updateName }
          .map { UserRepository[F].store } match {
          case Right(userF) =>
            userF map { user =>
              Right(UpdateUserResult(user.id.value))
            }
          case Left(err) =>
            pure(Left(err))
        }
      case None =>
        pure(Left(s"user($userId) not found."))
    }
  }
}

case class UpdateUserParam(id: String, name: String)

case class UpdateUserResult(id: String)
