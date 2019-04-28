package net.petitviolet.example.commons

import cats.data.NonEmptyList
import cats.data.Validated.{ Invalid, Valid }
import cats.syntax.{ FunctorSyntax, TupleSemigroupalSyntax }
import enumeratum.values._

object Validation extends TupleSemigroupalSyntax with FunctorSyntax {
  type Validated[A] = cats.data.ValidatedNel[String, A]

  def OK[A](a: A): Validated[A] = cats.data.Validated.Valid(a)

  def NG[A](errorMessage: String): Validated[A] =
    cats.data.Validated.Invalid(NonEmptyList(errorMessage, Nil))

  implicit class ValidatedFlatMap[A](val va: Validated[A]) extends AnyVal {
    def flatMap[B](f: A => Validated[B]): Validated[B] = {
      va match {
        case Valid(a)             => f(a)
        case invalid @ Invalid(_) => invalid
      }
    }
  }

  implicit class ValidatedFlatten[A](val vva: Validated[Validated[A]]) extends AnyVal {
    def flatten: Validated[A] = {
      vva match {
        case Valid(Valid(a))             => Valid(a)
        case Valid(invalid @ Invalid(_)) => invalid
        case invalid @ Invalid(_)        => invalid
      }
    }
  }

}
