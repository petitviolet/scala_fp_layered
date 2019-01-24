package net.petitviolet.example.domains.groups

import net.petitviolet.example.domains.{Repository, RepositoryResolver}

trait GroupRepository[F[_]] extends Repository[F, Group] {
  def findAll: F[Seq[Group]]
}

object GroupRepository extends RepositoryResolver[GroupRepository]
