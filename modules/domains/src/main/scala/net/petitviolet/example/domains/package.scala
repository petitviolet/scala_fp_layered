package net.petitviolet.example

import net.petitviolet.edatetime.EDateTime
import net.petitviolet.example.commons.DateTime

package object domains {
  def now(): DateTime = EDateTime.now()
}
