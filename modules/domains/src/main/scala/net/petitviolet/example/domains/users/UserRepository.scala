package net.petitviolet.example.domains.users

import net.petitviolet.example.domains.{ Repository, RepositoryResolver }

trait UserRepository[M[_]] extends Repository[M, User] {
  def findAll: M[Seq[User]]

  def findByEmail(email: User.Email): M[Option[User]]
}

object UserRepository extends RepositoryResolver[UserRepository]
