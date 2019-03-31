package net.petitviolet.example.applications

import java.time.ZonedDateTime

import net.petitviolet.example.domains.users.{User, UserRepository}
import net.petitviolet.operator.toPipe
import wvlet.airframe.bind

trait GetAllUserApplication[F[_]] extends Application[F] {
  private implicit val userRepository: UserRepository[F] =
    bind[UserRepository[F]]

  def execute(): F[GetAllUserResult] = {
    userRepository.findAll map { users: Seq[User] =>
      users.map { user =>
        UserResult(user.id.value,
                   user.name.value,
                   user.groupId.value,
                   user.createdAt)
      } |> GetAllUserResult.apply
    }
  }
}

case class GetAllUserResult(users: Seq[UserResult])

case class UserResult(id: String,
                      name: String,
                      groupId: String,
                      createdAt: ZonedDateTime)
