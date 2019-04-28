package net.petitviolet.example.applications

import cats.Monad
import net.petitviolet.example.applications.dtos.UserDto
import net.petitviolet.example.domains.users.{ User, UserRepository }
import net.petitviolet.operator.toPipe

class GetAllUserApplication[F[_]: UserRepository: Monad] extends Application[F] {
  def execute(): F[GetAllUserResult] = {
    UserRepository[F].findAll map { users: Seq[User] =>
      users.map { UserDto.convert } |> GetAllUserResult.apply
    }
  }
}

case class GetAllUserResult(users: Seq[UserDto])
