package net.petitviolet.example.commons.enums

import net.petitviolet.example.commons.Validation

abstract class StringEnumEntry extends enumeratum.values.StringEnumEntry
trait StringEnum[A <: StringEnumEntry] extends enumeratum.values.StringEnum[A] {
  def withValueValidated(value: String): Validation.Validated[A] =
    this.withValueOpt(value) match {
      case Some(v) => Validation.OK(v)
      case None    => Validation.NG(s"invalid value($value) for ${this.getClass.getSimpleName}")
    }
}
