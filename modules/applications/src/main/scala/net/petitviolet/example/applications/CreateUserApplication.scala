package net.petitviolet.example.applications

import cats.Monad
import net.petitviolet.example.applications.dtos.UserDto
import net.petitviolet.example.commons.Validation._
import net.petitviolet.example.domains.Context
import net.petitviolet.example.domains.users.{ User, UserRepository }

class CreateUserApplication[M[_]: Monad: UserRepository] extends Application[M] {
  def execute(param: CreateUserParam): M[Validated[UserDto]] = {
    val CreateUserParam(email, name, visibility) = param
    implicit val ctx: Context = Context.create()
    User.create(name, email, visibility).map {
      _.map { UserDto.convert }
    }
  }
}

case class CreateUserParam(email: String, name: String, visibility: String)
