package net.petitviolet.example.commons

import cats.Monad
import cats.syntax.{ FlatMapSyntax, FunctorSyntax }
import Validation._
import cats.data.Validated.{ Invalid, Valid }

object monadic extends FlatMapSyntax with FunctorSyntax {
  implicit class optionalMonadOps[M[_], A](val oma: Option[M[A]]) extends AnyVal {
    def flip(implicit M: Monad[M]): M[Option[A]] = oma match {
      case Some(ma) => ma map Option.apply
      case None     => M.pure(None)
    }
  }

  implicit class validatedMonadOps[M[_], A](val vma: Validated[M[A]]) extends AnyVal {
    def flip(implicit M: Monad[M]): M[Validated[A]] = {
      vma match {
        case Valid(ma)            => ma map OK
        case errMsgs @ Invalid(_) => Monad[M].pure(errMsgs)
      }
    }
  }

  implicit class nestedValidatedMonadOps[M[_], A](val vmva: Validated[M[Validated[A]]])
      extends AnyVal {
    def wrapUp(implicit M: Monad[M]): M[Validated[A]] = {
      vmva.flip.map { _.flatten }
    }
  }
}
