package net.petitviolet.example

import java.time.ZonedDateTime

package object domains {
  type DateTime = ZonedDateTime

  def now(): DateTime = ZonedDateTime.now()
}
