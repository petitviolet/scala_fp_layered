package net.petitviolet.example.domains.user_lists

import net.petitviolet.example.commons.DateTime
import net.petitviolet.example.commons.enums.{ StringEnum, StringEnumEntry }
import net.petitviolet.example.domains.{ Entity, Id }
import net.petitviolet.example.domains.user_lists.UserList._
import net.petitviolet.example.domains.users.User

sealed abstract case class UserList(
    id: Id[UserList],
    label: Label,
    owner: Id[User],
    visibility: Visibility,
    users: Set[Id[User]],
    createdAt: DateTime
) extends Entity { self =>

  private def copy(
      label: Label = self.label,
      visibility: Visibility = self.visibility,
      users: Set[Id[User]] = self.users
  ): this.type = {
    new UserList(
      self.id,
      label,
      self.owner,
      visibility,
      users,
      self.createdAt
    ) {}.asInstanceOf[this.type]
  }

  def addUser(user: User): this.type = {
    self.copy(users = users + user.id)
  }

  def removeUser(user: User): this.type = {
    self.copy(users = users - user.id)
  }
}

object UserList {
  private[domains] def apply(
      id: String,
      label: String,
      owner: String,
      visibility: String,
      users: Set[String],
      createdAt: DateTime
  ) = {
    new UserList(
      Id(id),
      Label(label),
      Id(owner),
      Visibility.withValue(visibility),
      users map { Id.apply[User] },
      createdAt
    ) {}
  }

  case class Label(value: String)
  sealed abstract class Visibility(val value: String) extends StringEnumEntry
  object Visibility extends StringEnum[Visibility] {
    case object Public extends Visibility("public")
    case object Private extends Visibility("private")

    override def values = findValues
  }
}
