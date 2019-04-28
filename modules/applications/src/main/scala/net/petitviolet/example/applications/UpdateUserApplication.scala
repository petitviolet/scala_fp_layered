package net.petitviolet.example.applications

import cats.Monad
import net.petitviolet.example.applications.dtos.UserDto
import net.petitviolet.example.commons.Validation
import net.petitviolet.example.commons.Validation.Validated
import net.petitviolet.example.commons.monadic._
import net.petitviolet.example.domains.Id
import net.petitviolet.example.domains.users.{ User, UserRepository }

class UpdateUserApplication[M[_]: UserRepository: Monad] extends Application[M] {

  def execute(param: UpdateUserParam): M[Validated[UserDto]] = {
    def pure[A](a: A): M[A] = Monad[M].pure(a)

    val UpdateUserParam(userId, newName) = param
    UserRepository[M].findById(Id(userId)).flatMap {
      case Some(user) =>
        User.Name
          .create(newName)
          .map { user.updateName }
          .map { UserRepository[M].store }
          .map {
            _.map { UserDto.convert }
          }
          .flip
      case None =>
        pure(Validation.NG(s"user($userId) not found."))
    }
  }
}

case class UpdateUserParam(id: String, name: String)
