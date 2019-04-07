package net.petitviolet.example.domains.users

import net.petitviolet.example.domains._
import net.petitviolet.example.domains.projects.Project
import net.petitviolet.example.domains.users.User._

sealed abstract case class User(id: Id[User],
                                name: Name,
                                projectIds: Set[Id[Project]],
                                createdAt: DateTime)
    extends Entity { self =>

  private def copy(
      name: Name = self.name,
      projectIds: Set[Id[Project]] = self.projectIds): this.type = {
    new User(id = self.id, name, projectIds, self.createdAt) {}
      .asInstanceOf[this.type]
  }

  def updateName(newName: Name): this.type = copy(name = newName)

  def joinProject(project: Project): this.type =
    copy(projectIds = projectIds + project.id)

  def leaveProject(project: Project): this.type =
    copy(projectIds = projectIds - project.id)
}

object User {
  private[domains] def apply(id: String,
                             name: String,
                             groupId: String,
                             createdAt: DateTime): User = {
    new User(Id(id), new Name(name), Set(Id[Project](groupId)), createdAt) {}
  }

  def create(name: String): Either[Seq[String], User] = {
    Name.create(name).left.map { _ :: Nil }.map { name =>
      new User(Id.generate, name, Set.empty, now()) {}
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
