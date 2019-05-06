package net.petitviolet.example.domains.user_lists

import net.petitviolet.example.domains.users.User
import net.petitviolet.example.domains.{ Id, Repository }

trait UserListRepository[F[_]] extends Repository[F, UserList] {
  def findByOwnerId(ownerId: Id[User]): F[Seq[UserList]]
}
