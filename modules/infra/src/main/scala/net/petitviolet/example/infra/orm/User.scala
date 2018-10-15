package net.petitviolet.example.infra.orm

import net.petitviolet.example.infra.DateTime
import scalikejdbc.{DBSession, WrappedResultSet, autoConstruct}
import skinny.orm.Alias

case class User(id: String,
                name: String,
                createdAt: DateTime,
                updatedAt: DateTime)

object User extends ORMapper[User] {

  override val _tableName: String = "user"

  override val defaultAlias: Alias[User] = syntax("u")

  override def extract(rs: WrappedResultSet,
                       n: scalikejdbc.ResultName[User]): User =
    autoConstruct(rs, n)

  def insert(user: User)(implicit s: DBSession) = {
    createWithAttributes(
      'id -> user.id,
      'name -> user.name,
      'createdAt -> user.createdAt,
      'updatedAt -> user.updatedAt,
    )
  }
}
