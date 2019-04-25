package net.petitviolet.example.applications

import java.time.ZonedDateTime

import cats.Monad
import net.petitviolet.example.domains.users.{User, UserRepository}
import net.petitviolet.operator.toPipe

class GetAllUserApplication[F[_]: UserRepository: Monad]
    extends Application[F] {
  def execute(): F[GetAllUserResult] = {
    UserRepository[F].findAll map { users: Seq[User] =>
      users.map { user =>
        UserResult(user.id.value, user.name.value, user.projectIds.map {
          _.value
        }, user.createdAt)
      } |> GetAllUserResult.apply
    }
  }
}

case class GetAllUserResult(users: Seq[UserResult])

case class UserResult(id: String,
                      name: String,
                      projectIds: Set[String],
                      createdAt: ZonedDateTime)
