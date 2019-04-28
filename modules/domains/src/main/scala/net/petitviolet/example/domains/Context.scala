package net.petitviolet.example.domains

import net.petitviolet.edatetime.EDateTime
import net.petitviolet.example.commons.DateTime
import net.petitviolet.example.domains.users.User

sealed trait Context {
  def now: DateTime
}

object Context {
  def create(): AnonymousContext = AnonymousContext(EDateTime.now())
  def create(user: User): AuthenticatedContext =
    AuthenticatedContext(user, EDateTime.now())
}

final case class AuthenticatedContext(user: User, now: DateTime) extends Context
final case class AnonymousContext(now: DateTime) extends Context
