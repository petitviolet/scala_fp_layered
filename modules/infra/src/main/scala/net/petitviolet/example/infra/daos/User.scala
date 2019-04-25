package net.petitviolet.example.infra.daos

import net.petitviolet.example.infra.DateTime
import scalikejdbc.{DBSession, WrappedResultSet, autoConstruct}
import skinny.orm.Alias

case class User(id: String,
                name: String,
                createdAt: DateTime,
                updatedAt: DateTime)

object User extends ORMapperWithStringId[User] {

  override protected val _tableName: String = "user"

  override val defaultAlias: Alias[User] = syntax("u")

  override def extract(rs: WrappedResultSet,
                       n: scalikejdbc.ResultName[User]): User =
    autoConstruct(rs, n)

  def insert(user: User)(implicit s: DBSession): Unit = {
    createWithNamedValues(
      column.id -> user.id,
      column.name -> user.name,
      column.createdAt -> user.createdAt,
      column.updatedAt -> user.updatedAt,
    )
    ()
  }
}
