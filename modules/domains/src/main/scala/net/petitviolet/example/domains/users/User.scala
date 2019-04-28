package net.petitviolet.example.domains.users

import enumeratum.values.{ IntEnum, IntEnumEntry }
import net.petitviolet.edatetime.EDateTime
import net.petitviolet.example.commons.Validation._
import net.petitviolet.example.domains._
import net.petitviolet.example.domains.users.User._

sealed abstract case class User(
  id: Id[User],
  email: Email,
  name: Name,
  status: Status,
  visibility: Visibility,
  createdAt: DateTime
) extends Entity { self =>

  private def copy(
    name: Name = self.name,
    email: Email = self.email,
    status: Status = self.status,
    visibility: Visibility = self.visibility,
  ): this.type = {
    new User(
      id = self.id,
      email = email,
      name = name,
      status = status,
      visibility = visibility,
      createdAt = self.createdAt
    ) {}
      .asInstanceOf[this.type]
  }

  def updateName(newName: Name): this.type = copy(name = newName)
}

object User {
  private[domains] def apply(id: String,
    email: String,
    name: String,
    status: Int,
    visibility: Int,
    createdAt: DateTime): User = {
    new User(Id(id), Email(email), Name(name), Status.withValue(status), Visibility.withValue(visibility), createdAt) {}
  }

  def create(name: String, email: String, visibility: Visibility): Validated[User] = {
    (Name.create(name), Email.create(email)) mapN { (name, email) =>
      new User(Id.generate, email, name, Status.Temporal, visibility, EDateTime.now()) {}
    }
  }

  case class Email(value: String) extends AnyVal

  object Email {
    private val REGEX = """^\w+@\w[\w\.]+\w$""".r

    def create(value: String) = {
      if (REGEX.findFirstIn(value).isDefined) {
        OK(new Email(value))
      } else {
        NG("email is invalid")
      }
    }
  }

  case class Name(value: String) extends AnyVal

  object Name {
    private val MAX_LENGTH = 30

    def create(name: String): Validated[Name] = {
      if (name.nonEmpty && name.length <= MAX_LENGTH) {
        OK(new Name(name))
      } else NG("user name is invalid!")
    }
  }

  sealed abstract class Status(val value: Int) extends IntEnumEntry

  object Status extends IntEnum[Status] {
    case object Temporal extends Status(1)
    case object Activated extends Status(2)
    case object Banned extends Status(3)
    case object Quit extends Status(4)

    override def values= findValues
  }

  sealed abstract class Visibility(val value: Int) extends IntEnumEntry
  object Visibility extends IntEnum[Visibility] {
    case object Public extends Visibility(1)
    case object Private extends Visibility(2)

    override def values= findValues
  }
}
