package net.petitviolet.example.domains.users

import net.petitviolet.example.domains.projects.Project
import net.petitviolet.example.domains.{Id, Repository, RepositoryResolver}

trait UserRepository[F[_]] extends Repository[F, User] {
  def findAll: F[Seq[User]]

  def findAllByGroup(groupId: Id[Project]): F[Seq[User]]
}

object UserRepository extends RepositoryResolver[UserRepository]
