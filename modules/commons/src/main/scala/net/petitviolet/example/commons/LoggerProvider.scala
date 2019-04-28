package net.petitviolet.example.commons

import org.slf4j.{ Logger, LoggerFactory }

trait LoggerProvider {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
}
