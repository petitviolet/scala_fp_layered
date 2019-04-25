package net.petitviolet.example.applications

import cats.Monad
import net.petitviolet.example.domains.Id
import net.petitviolet.example.domains.users.{User, UserRepository}

class UpdateUserApplication[F[_]: UserRepository: Monad]
    extends Application[F] {

  def execute(param: UpdateUserParam): F[Either[String, UpdateUserResult]] = {
    def pure[A](a: A): F[A] = Monad[F].pure(a)

    val UpdateUserParam(userId, newName) = param
    UserRepository[F].findById(Id(userId)) flatMap {
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
