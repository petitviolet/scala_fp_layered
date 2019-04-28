package net.petitviolet.example.applications.dtos

import net.petitviolet.example.commons.DateTime
import net.petitviolet.example.domains.users.User

case class UserDto(id: String,
                   email: String,
                   name: String,
                   status: String,
                   visibility: String,
                   createdAt: DateTime)

object UserDto {
  def convert(d: User): UserDto = {
    apply(
      d.id.value,
      d.email.value,
      d.name.value,
      d.status.value,
      d.visibility.value,
      d.createdAt
    )
  }
}
