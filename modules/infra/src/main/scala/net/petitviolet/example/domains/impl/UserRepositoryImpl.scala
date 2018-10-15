package net.petitviolet.example.domains.impl

import net.petitviolet.example.infra.orm
import net.petitviolet.example.domains.Id
import net.petitviolet.example.domains.users.{User, UserRepository}
import net.petitviolet.example.infra.orm.Database

import scala.concurrent.Future

object UserRepositoryImpl extends UserRepository[AsyncIO] {
  private def dto2domain(dto: orm.User): User = {
    User.apply(dto.id, dto.name, dto.createdAt)
  }

  override def findAll: AsyncIO[Seq[User]] = scalaz.Kleisli { implicit ctx =>
    Future {
      orm.User.findAll() map dto2domain
    }
  }

  override def findById(id: Id[User]): AsyncIO[Option[User]] = {
    scalaz.Kleisli { implicit ctx: Ctx =>
      Future {
        orm.User.findById(id.value) map dto2domain
      }
    }
  }

  override def store(entity: User): AsyncIO[User] = {
    scalaz.Kleisli { implicit ctx =>
      Future {
        orm.User.deleteById(entity.id.value)
        orm.User.createWithAttributes(
          'id -> entity.id.value,
          'name -> entity.name.value,
          'createdAt -> entity.createdAt,
          'updatedAt -> Database.now()
        )
        entity
      }
    }
  }
}
