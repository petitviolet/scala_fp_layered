package net.petitviolet.example.queries

import cats.Monad
import net.petitviolet.example.applications.dtos.UserDto
import net.petitviolet.example.domains.users.{ User, UserRepository }
import net.petitviolet.operator.toPipe

class GetAllUserQuery[F[_]: UserRepository: Monad] extends Query[F] {
  def execute(): F[GetAllUserResult] = {
    UserRepository[F].findAll map { users: Seq[User] =>
      users.map { UserDto.convert } |> GetAllUserResult.apply
    }
  }
}

case class GetAllUserResult(users: Seq[UserDto])
