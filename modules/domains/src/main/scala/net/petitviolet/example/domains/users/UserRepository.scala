package net.petitviolet.example.domains.users

import net.petitviolet.example.domains.{ Repository, RepositoryResolver }

trait UserRepository[F[_]] extends Repository[F, User]

object UserRepository extends RepositoryResolver[UserRepository]
