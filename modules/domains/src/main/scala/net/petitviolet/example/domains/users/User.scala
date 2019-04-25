package net.petitviolet.example.domains.users

import net.petitviolet.example.domains._
import net.petitviolet.example.domains.users.User._

sealed abstract case class User(id: Id[User],
                                name: Name,
                                createdAt: DateTime)
    extends Entity { self =>

  private def copy(
      name: Name = self.name): this.type = {
    new User(id = self.id, name, self.createdAt) {}
      .asInstanceOf[this.type]
  }

  def updateName(newName: Name): this.type = copy(name = newName)
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
