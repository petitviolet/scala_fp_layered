package net.petitviolet.example.domains.impl

import cats.data.Kleisli
import net.petitviolet.example.infra.daos
import net.petitviolet.edatetime.EDateTime
import net.petitviolet.example.domains.Id
import net.petitviolet.example.domains.users.{ User, UserRepository }
import net.petitviolet.example.infra.daos.Database

import scala.concurrent.Future

object UserRepositoryImpl extends UserRepository[AsyncIO] {
  private def dto2domain(dto: daos.User): User = {
    User.apply(dto.id, dto.email, dto.name, dto.status, dto.visibility, EDateTime(dto.createdAt))
  }

  override def findAll: AsyncIO[Seq[User]] = Kleisli { implicit ctx =>
    Future {
      daos.User.findAll() map dto2domain: Seq[User]
    }
  }

  override def findById(id: Id[User]): AsyncIO[Option[User]] = {
    Kleisli { implicit ctx: Ctx =>
      Future {
        daos.User.findById(id.value) map dto2domain
      }
    }
  }

  override def store(entity: User): AsyncIO[User] = {
    Kleisli { implicit ctx =>
      Future {
        daos.User.deleteById(entity.id.value)
        daos.User.createWithAttributes(
          'id -> entity.id.value,
          'name -> entity.name.value,
          'createdAt -> entity.createdAt.value,
          'updatedAt -> Database.now()
        )
        entity
      }
    }
  }

  override def findByEmail(email: User.Email): AsyncIO[Option[User]] = ???
}
