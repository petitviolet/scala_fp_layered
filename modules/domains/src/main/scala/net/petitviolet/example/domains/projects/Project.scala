package net.petitviolet.example.domains.projects

import net.petitviolet.example.domains.{DateTime, Entity, Id}

sealed abstract case class Project(id: Id[Project],
                                   name: Project.Name,
                                   createdAt: DateTime)
    extends Entity {}

object Project {
  case class Name private[projects] (value: String)

  object Name {
    private val MAX_LENGTH = 20

    def create(name: String): Either[String, Name] = {
      if (name.nonEmpty && name.length <= MAX_LENGTH) {
        Right(new Name(name))
      } else Left("group name is invalid!")
    }
  }
}
