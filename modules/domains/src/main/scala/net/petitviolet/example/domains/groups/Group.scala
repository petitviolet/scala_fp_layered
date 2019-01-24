package net.petitviolet.example.domains.groups

import net.petitviolet.example.domains.{DateTime, Entity, Id}

sealed abstract case class Group(id: Id[Group],
                                 name: Group.Name,
                                 createdAt: DateTime)
    extends Entity {}

object Group {
  case class Name private[groups] (value: String)

  object Name {
    private val MAX_LENGTH = 20

    def create(name: String): Either[String, Name] = {
      if (name.nonEmpty && name.length <= MAX_LENGTH) {
        Right(new Name(name))
      } else Left("group name is invalid!")
    }
  }
}
