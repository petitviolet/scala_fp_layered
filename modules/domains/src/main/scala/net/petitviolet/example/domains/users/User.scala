package net.petitviolet.example.domains.users

import cats.Monad
import net.petitviolet.example.commons.DateTime
import net.petitviolet.example.commons.Validation._
import net.petitviolet.example.commons.enums.{ StringEnum, StringEnumEntry }
import net.petitviolet.example.domains._
import net.petitviolet.example.domains.users.User._

sealed abstract case class User(
    id: Id[User],
    email: Email,
    name: Name,
    status: Status,
    visibility: Visibility,
    createdAt: DateTime
) extends Entity {
  self =>

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
                             status: String,
                             visibility: String,
                             createdAt: DateTime): User = {
    new User(
      Id(id),
      Email(email),
      Name(name),
      Status.withValue(status),
      Visibility.withValue(visibility),
      createdAt
    ) {}
  }

  def create[M[_]: Monad: UserRepository](email: String, name: String, visibility: String)(
      implicit ctx: Context): M[Validated[User]] = {
    import net.petitviolet.example.commons.monadic._

    (Email.create(email), Name.create(name), Visibility.withValueValidated(visibility)).mapN {
      (email, name, visibility) =>
        UserRepository[M].findByEmail(email).map {
          case None =>
            OK(new User(Id.generate, email, name, Status.Temporal, visibility, ctx.now) {})

          case Some(user) =>
            NG(s"email(${email.value}) has already registered")
        }
    }.wrapUp
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

  sealed abstract class Status(val value: String) extends StringEnumEntry

  object Status extends StringEnum[Status] {
    case object Temporal extends Status("temporal")
    case object Activated extends Status("activated")
    case object Banned extends Status("banned")
    case object Quit extends Status("quit")

    override def values = findValues
  }

  sealed abstract class Visibility(val value: String) extends StringEnumEntry
  object Visibility extends StringEnum[Visibility] {
    case object Public extends Visibility("public")
    case object Private extends Visibility("private")

    override def values = findValues
  }
}
