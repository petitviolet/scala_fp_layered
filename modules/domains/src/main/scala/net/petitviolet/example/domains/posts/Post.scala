package net.petitviolet.example.domains.posts

import net.petitviolet.example.domains.{ DateTime, Entity, Id }
import net.petitviolet.example.domains.users.User

sealed abstract case class Post(
  id: Id[Post],
  createdBy: Id[User],
  text: Post.Text,
  createdAt: DateTime,
  updatedAt: DateTime
) extends Entity {

}

object Post {
  private[domain] def apply(id: Id[Post], createdBy: Id[User], text: String, createdAt: DateTime, updatedAt: DateTime) = {
    new Post(id, createdBy, Text(text), createdAt, updatedAt) {}
  }

  case class Text(value: String)
  object Text {
    def create(value:String): Either[Seq[String], Text] = {
      
    }
  }
}
