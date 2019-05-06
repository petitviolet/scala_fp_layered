package net.petitviolet.example.domains.impl

import cats.data.Kleisli
import net.petitviolet.example.infra.daos
import net.petitviolet.edatetime.EDateTime
import net.petitviolet.example.domains.Id
import net.petitviolet.example.domains.users.{ User, UserRepository }
import net.petitviolet.example.infra.daos.Database

import scala.concurrent.Future

object UserRepositoryImpl extends UserRepository[AsyncIO] {
  override def findById(id: Id[User]): AsyncIO[Option[User]] = {
    Kleisli {
      case (dbSession, ec) =>
        Future {
          daos.User.findById(id.value)(dbSession) map dto2domain
        }(ec)
    }
  }

  override def store(entity: User): AsyncIO[User] = {
    Kleisli {
      case (dbSession, ec) =>
        Future {
//          daos.User.store(domain2dto(entity))(dbSession)
          // delete + insert
          daos.User.deleteById(entity.id.value)(dbSession)
          daos.User.insert(domain2dto(entity))(dbSession)
          entity
        }(ec)
    }
  }

  private def dto2domain(dto: daos.User): User = {
    User.apply(dto.id, dto.email, dto.name, dto.status, dto.visibility, EDateTime(dto.createdAt))
  }
  private def domain2dto(domain: User): daos.User = {
    daos.User(
      domain.id.value,
      domain.email.value,
      domain.name.value,
      domain.status.value,
      domain.visibility.value,
      domain.createdAt.value,
      Database.now()
    )
  }

  override def findAll: AsyncIO[Seq[User]] = Kleisli { implicit ctx =>
    Future {
      daos.User.findAll() map dto2domain: Seq[User]
    }
  }

  override def findByEmail(email: User.Email): AsyncIO[Option[User]] = ???
}
