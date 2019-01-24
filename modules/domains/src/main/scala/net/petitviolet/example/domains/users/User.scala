package net.petitviolet.example.domains.users

import net.petitviolet.example.domains._
import net.petitviolet.example.domains.groups.Group
import net.petitviolet.example.domains.users.User._

sealed abstract case class User(id: Id[User],
                                name: Name,
                                groupId: Id[Group],
                                createdAt: DateTime)
    extends Entity {
  def updateName(newName: Name): this.type =
    new User(this.id, newName, groupId, createdAt) {}.asInstanceOf[this.type]

  def changeGroup(newGroup: Group): this.type =
    new User(this.id, name, newGroup.id, createdAt) {}.asInstanceOf[this.type]
}

object User {
  private[domains] def apply(id: String,
                             name: String,
                             groupId: String,
                             createdAt: DateTime): User = {
    new User(Id(id), new Name(name), Id[Group](groupId), createdAt) {}
  }

  def create(name: String, group: Group): Either[Seq[String], User] = {
    Name.create(name).left.map { _ :: Nil }.map { name =>
      new User(Id.generate, name, group.id, now()) {}
    }
  }

  class Name private[User] (val value: String)

  object Name {
    private val MAX_LENGTH = 30

    def create(name: String): Either[String, Name] = {
      if (name.nonEmpty && name.length <= MAX_LENGTH) {
        Right(new Name(name))
      } else Left("user name is invalid!")
    }
  }
}
