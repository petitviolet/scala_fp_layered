package net.petitviolet.example.applications

import cats.Monad
import net.petitviolet.example.domains.user_lists.UserListRepository
import net.petitviolet.example.domains.users.repo.Pure.UserRepository

//class EditUserListApplication[F[_]: Monad: UserRepository: UserListRepository]
//    extends Application[F] {
//
//  def execute()
//
//}

case class EditUserListParam(
    newLabel: Option[String] = None,
    userIdsToAdd: Set[String] = Set.empty,
    userIdsToRemove: Set[String] = Set.empty
) {
  def isValid: Boolean = newLabel.isDefined || userIdsToAdd.nonEmpty || userIdsToRemove.nonEmpty
}
