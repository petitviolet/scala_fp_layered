package net.petitviolet.example.domains.projects

import net.petitviolet.example.domains.{Repository, RepositoryResolver}

trait ProjectRepository[F[_]] extends Repository[F, Project] {
  def findAll: F[Seq[Project]]
}

object ProjectRepository extends RepositoryResolver[ProjectRepository]
