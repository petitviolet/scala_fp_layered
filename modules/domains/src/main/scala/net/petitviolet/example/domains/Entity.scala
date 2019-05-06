package net.petitviolet.example.domains

import java.util.UUID

import net.petitviolet.example.commons.DateTime

trait Entity { self =>
  def id: Id[_] // cut-corner
  def createdAt: DateTime

  override def hashCode(): Int = id.##

  def canEqual(other: Any): Boolean = other.getClass == self.getClass // should override by descendant

  override final def equals(obj: scala.Any): Boolean = {
    canEqual(obj) && obj.asInstanceOf[Entity].id == self.id
  }
}

case class Id[A](value: String)

object Id {
  def generate[A]: Id[A] = Id[A](UUID.randomUUID().toString)
}
