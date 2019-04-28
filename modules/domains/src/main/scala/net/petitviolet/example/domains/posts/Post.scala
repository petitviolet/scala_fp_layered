package net.petitviolet.example.domains.posts

import net.petitviolet.example.commons.{ DateTime, Validation }
import net.petitviolet.example.domains.users.User
import net.petitviolet.example.domains.{ AuthenticatedContext, Entity, Id }
import net.petitviolet.operator.toIntOps

sealed abstract case class Post(
    id: Id[Post],
    createdBy: Id[User],
    text: Post.Text,
    createdAt: DateTime,
    updatedAt: DateTime
) extends Entity {}

object Post {
  private[posts] def apply(id: Id[Post],
                           createdBy: Id[User],
                           text: String,
                           createdAt: DateTime,
                           updatedAt: DateTime) = {
    new Post(id, createdBy, Text(text), createdAt, updatedAt) {}
  }

  def create(text: String)(
      implicit ctx: AuthenticatedContext
  ): Validation.Validated[Post] = {
    Text.create(text).map { text =>
      new Post(
        Id.generate,
        ctx.user.id,
        text,
        ctx.now,
        ctx.now
      ) {}
    }
  }

  case class Text(value: String) extends AnyVal
  object Text {
    private val MAX_LENGTH = 140

    def create(value: String): Validation.Validated[Text] = {
      if (value.length.between(1, MAX_LENGTH)) {
        Validation.OK(apply(value))
      } else {
        Validation.NG(s"post.text length must be between 1 and $MAX_LENGTH")
      }
    }
  }
}
