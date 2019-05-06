package net.petitviolet.example.commons

import cats.Monad
import cats.syntax.{ FlatMapSyntax, FunctorSyntax }
import Validation._
import cats.data.Validated.{ Invalid, Valid }

object monadic extends FlatMapSyntax with FunctorSyntax {
  implicit class optionalMonadOps[F[_], A](val oma: Option[F[A]]) extends AnyVal {
    def flip(implicit M: Monad[F]): F[Option[A]] = oma match {
      case Some(ma) => ma map Option.apply
      case None     => M.pure(None)
    }
  }

  implicit class validatedMonadOps[F[_], A](val vma: Validated[F[A]]) extends AnyVal {
    def flip(implicit M: Monad[F]): F[Validated[A]] = {
      vma match {
        case Valid(ma)            => ma map OK
        case errMsgs @ Invalid(_) => Monad[F].pure(errMsgs)
      }
    }
  }

  implicit class nestedValidatedMonadOps[F[_], A](val vmva: Validated[F[Validated[A]]])
      extends AnyVal {
    def wrapUp(implicit M: Monad[F]): F[Validated[A]] = {
      vmva.flip.map { _.flatten }
    }
  }
}
