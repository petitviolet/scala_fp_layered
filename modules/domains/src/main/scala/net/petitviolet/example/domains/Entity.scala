package net.petitviolet.example.domains

import java.util.UUID

trait Entity { self =>
  def id: Id[_]
  def createdAt: DateTime

  override final def equals(obj: scala.Any): Boolean = {
    self.getClass == obj.getClass &&
    obj.asInstanceOf[Entity].id == self.id
  }
}

case class Id[A](value: String)

object Id {
  def generate[A]: Id[A] = Id[A](UUID.randomUUID().toString)
}
