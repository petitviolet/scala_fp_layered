package net.petitviolet.example.domains.users

import net.petitviolet.example.domains._
import net.petitviolet.example.domains.users.User._

sealed abstract case class User(id: Id[User], name: Name, createdAt: DateTime)
    extends Entity {
  def updateName(newName: Name): this.type =
    new User(this.id, newName, createdAt) {}.asInstanceOf[this.type]
}

object User {
  private[domains] def apply(id: String,
                             name: String,
                             createdAt: DateTime): User = {
    new User(Id(id), new Name(name), createdAt) {}
  }

  def create(name: String): Either[Seq[String], User] = {
    Name.create(name).left.map { _ :: Nil }.map { name =>
      new User(Id.generate, name, now()) {}
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
