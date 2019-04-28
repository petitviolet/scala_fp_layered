package net.petitviolet.example.infra.daos

import net.petitviolet.example.infra.DateTime
import scalikejdbc.{ autoConstruct, WrappedResultSet }
import skinny.orm.Alias

case class UserList(id: String,
                    ownerId: String,
                    name: String,
                    createdAt: DateTime,
                    updatedAt: DateTime,
                    ownerOpt: Option[User] = None)

object UserList extends ORMapperWithStringId[UserList] {
  override protected val _tableName: String = "user_list"

  override val defaultAlias: Alias[UserList] = syntax("ul")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[UserList]): UserList =
    autoConstruct(rs, n, "ownerOpt")

}

case class UserListRel(userListId: String, userId: String, createdAt: DateTime)

object UserListRel extends ORMapperRel[UserListRel] {
  override protected val _tableName: String = "user_list_rel"

  override val defaultAlias: Alias[UserListRel] = syntax("ulr")
}
