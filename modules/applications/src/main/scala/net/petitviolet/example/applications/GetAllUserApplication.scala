package net.petitviolet.example.applications

import java.time.ZonedDateTime

import net.petitviolet.example.domains.users.{User, UserRepository}
import scalaz.Monad
import scalaz.syntax.ToBindOps
import net.petitviolet.operator.toPipe
import wvlet.airframe.bind

trait GetAllUserApplication[F[_]] extends ToBindOps {
  private implicit val userRepository: UserRepository[F] =
    bind[UserRepository[F]]
  private implicit val M: Monad[F] = bind[Monad[F]]

  def execute(): F[GetAllUserResult] = {
    userRepository.findAll map { users: Seq[User] =>
      users.map { user =>
        UserResult(user.id.value, user.name.value, user.createdAt)
      } |> GetAllUserResult.apply
    }
  }
}

case class GetAllUserResult(users: Seq[UserResult])

case class UserResult(id: String, name: String, createdAt: ZonedDateTime)
