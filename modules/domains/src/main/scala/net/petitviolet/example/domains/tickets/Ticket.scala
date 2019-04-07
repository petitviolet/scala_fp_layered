package net.petitviolet.example.domains.tickets

import enumeratum.values.{IntEnum, IntEnumEntry}
import net.petitviolet.example.domains.tickets.Ticket._
import net.petitviolet.example.domains.{DateTime, Entity, Id}

import scala.collection.immutable

sealed abstract case class Ticket(
    id: Id[Ticket],
    title: Title,
    description: Description,
    deadLine: Option[DeadLine],
    status: Status,
    createdAt: DateTime
) extends Entity { self =>
  private def copy(title: Title = self.title,
                   description: Description = self.description,
                   status: Status = self.status): this.type = {
    new Ticket(id, title, description, deadLine, status, createdAt) {}
      .asInstanceOf[this.type]
  }

  def changeStatus(status: Status): this.type = copy(status = status)
}

object Ticket {
  case class Title(value: String)

  case class Description(value: String)

  case class DeadLine(value: DateTime)

  sealed abstract class Status(val value: Int) extends IntEnumEntry
  object Status extends IntEnum[Status] {
    case object Todo extends Status(1)
    case object Doing extends Status(2)
    case object Done extends Status(3)
    override def values: immutable.IndexedSeq[Status] = findValues
  }
}
