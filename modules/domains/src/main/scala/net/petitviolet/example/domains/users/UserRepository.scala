package net.petitviolet.example.domains.users

import net.petitviolet.example.domains.users.repo.Pure.Hoge
import net.petitviolet.example.domains.{ Repository, RepositoryResolver }

trait UserRepository[F[_]] extends Repository[F, User] {
  def findAll: F[Seq[User]]

  def findByEmail(email: User.Email): F[Option[User]]
}

object UserRepository extends RepositoryResolver[UserRepository]

object repo {
  case class UserId(value: String) extends AnyVal
  case class UserName(value: String) extends AnyVal
  case class User(id: UserId, name: UserName)

  trait DBSession
  object DB {
    def readOnly[A](f: DBSession => A): A = ???
    def localTx[A](f: DBSession => A): A = ???
  }

  class UserDto {}
  object UserDao {
    def findById(userId: String)(implicit s: DBSession): Option[UserDto] = ???
    def insert(user: UserDto)(implicit s: DBSession): Unit = ???
    def update(user: UserDto)(implicit s: DBSession): Unit = ???
  }
  def dto2Domain(userDao: UserDto): User = ???
  def domain2Dto(user: User): UserDto = ???

  import scala.concurrent._

//  trait UserRepository {
//    def findById(userId: UserId): Option[User]
//
//    def store(user: User): Unit
//  }
//  object UserRepositoryImpl extends UserRepository {
//    override def findById(userId: UserId): Option[User] = {
//      DB readOnly { implicit dbSession =>
//        UserDao.findById(userId.value) map { dto2Domain }
//      }
//    }
//
//    override def store(user: User): Unit = {
//      DB localTx { implicit  dbSession =>
//        UserDao.findById(user.id.value) match {
//          case Some(_) => UserDao.update(domain2Dto(user))
//          case None => UserDao.insert(domain2Dto(user))
//        }
//      }
//    }
//  }
  object Impure {
    trait UserRepository {
      def findById(userId: UserId)(implicit ec: ExecutionContext,
                                   s: DBSession): Future[Option[User]]

      def store(user: User)(implicit ec: ExecutionContext, s: DBSession): Future[Unit]
    }
    class UserApplication(userRepository: UserRepository) { // DI
      def updateName(userId: String, newName: String)(implicit s: DBSession,
                                                      ec: ExecutionContext) = {
        val userOpt: Future[Option[User]] =
          userRepository.findById(UserId(userId))
        userOpt.map { user =>
          ??? // update user name process
        }
      }
    }
  }
  object Pure {
    import cats._
    trait UserRepository[F[_]] {
      def findById(userId: UserId): F[Option[User]]

      def store(user: User): F[Unit]
    }
    object UserRepository {
      @inline def apply[F[_]: UserRepository]: UserRepository[F] = implicitly
    }

    class UserApplication[F[_]: Monad: UserRepository] {
      def updateName(userId: String, newName: String) = {
        val userOptF: F[Option[User]] =
          UserRepository[F].findById(UserId(userId))

        cats.Monad[F].map(userOptF) { user =>
          ??? // update user name process
        }
      }
    }
  }
  object Airframe {
    import cats._
    trait UserRepository[F[_]] {
      def findById(userId: UserId): F[Option[User]]

      def store(user: User): F[Unit]
    }

    trait UserApplication[F[_]] {
      import wvlet.airframe.bind
      implicit val M: Monad[F] = bind[Monad[F]]
      val repo: UserRepository[F] = bind[UserRepository[F]]

      def updateName(userId: String, newName: String) = {
        val userOptF: F[Option[User]] =
          repo.findById(UserId(userId))

        cats.Monad[F].map(userOptF) { user =>
          ??? // update user name process
        }
      }
    }
  }
}
