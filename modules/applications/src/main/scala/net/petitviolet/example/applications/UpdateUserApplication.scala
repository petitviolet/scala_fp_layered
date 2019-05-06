package net.petitviolet.example.applications

import cats.Monad
import net.petitviolet.example.applications.dtos.UserDto
import net.petitviolet.example.commons.Validation
import net.petitviolet.example.commons.Validation.Validated
import net.petitviolet.example.commons.monadic._
import net.petitviolet.example.domains.Id
import net.petitviolet.example.domains.users.{ User, UserRepository }

trait UpdateUserApplication[F[_]] extends Application[F] {
  import wvlet.airframe.bind
  implicit val M: Monad[F] = bind[Monad[F]]
  implicit val userRepository: UserRepository[F] = bind[UserRepository[F]]

  def execute(param: UpdateUserParam): F[Validated[UserDto]] = {
    def pure[A](a: A): F[A] = Monad[F].pure(a)

    val UpdateUserParam(userId, newName) = param
    userRepository.findById(Id(userId)).flatMap {
      case Some(user) =>
        User.Name
          .create(newName)
          .map { user.updateName }
          .map { userRepository.store }
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
