package net.petitviolet.example.infra.daos

import net.petitviolet.example.infra.DateTime
import scalikejdbc.{ autoConstruct, WrappedResultSet }
import skinny.orm.Alias

case class Post(id: String,
                createdBy: String,
                text: String,
                createdAt: DateTime,
                userOpt: Option[User] = None)

object Post extends ORMapperWithStringId[Post] {
  override protected val _tableName: String = "post"

  override val defaultAlias: Alias[Post] = syntax("p")

  val userRef =
    belongsToWithFk[User](User, "created_by", (p, u) => p.copy(userOpt = u))

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Post]): Post =
    autoConstruct(rs, n, "userOpt")

  def insert(post: Post): Unit = {
    createWithNamedValues(
      column.id -> post.id,
      column.createdBy -> post.createdBy,
      column.text -> post.text,
      column.createdAt -> post.createdAt
    )
    ()
  }
}
