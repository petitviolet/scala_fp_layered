package net.petitviolet.example.applications

import net.petitviolet.example.domains.Id
import net.petitviolet.example.domains.users.{ User, UserRepository }
import scalaz.Monad
import scalaz.syntax.ToBindOps

class UpdateUserApplication[F[_] : Monad : UserRepository] extends ToBindOps {
  def execute(param: UpdateUserParam): F[Either[String, UpdateUserResult]] = {
    def pure[A](a:A): F[A] = implicitly[Monad[F]].pure(a)

    val UpdateUserParam(userId, newName) = param
    UserRepository[F].findById(Id(userId)) flatMap {
      case Some(user) =>
        User.Name.create(newName) map { user.updateName } map {
          user =>
            UserRepository[F].store(user)
        } match {
          case Right(userF) =>
            userF map { user => Right(UpdateUserResult(user.id.value)) }
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
