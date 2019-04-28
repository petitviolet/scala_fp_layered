package net.petitviolet.example

import net.petitviolet.edatetime.EDateTime

package object domains {
  type DateTime = EDateTime

  def now(): DateTime = EDateTime.now()
}
