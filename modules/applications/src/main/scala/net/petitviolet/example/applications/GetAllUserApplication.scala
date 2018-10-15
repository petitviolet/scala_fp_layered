package net.petitviolet.example.applications

import java.time.ZonedDateTime

import net.petitviolet.example.domains.users.{User, UserRepository}
import scalaz.Monad
import scalaz.syntax.ToBindOps
import net.petitviolet.operator.toPipe

class GetAllUserApplication[F[_]: Monad: UserRepository] extends ToBindOps {
  def execute(): F[GetAllUserResult] = {
    UserRepository[F].findAll map { users: Seq[User] =>
      users.map { user =>
        UserResult(user.id.value, user.name.value, user.createdAt)
      } |> GetAllUserResult.apply
    }
  }
}

case class GetAllUserResult(users: Seq[UserResult])

case class UserResult(id: String, name: String, createdAt: ZonedDateTime)
