package net.petitviolet.example.applications

import cats.Monad
import cats.syntax.{FlatMapSyntax, FunctorSyntax}

trait Application[F[_]] extends FunctorSyntax with FlatMapSyntax {
  protected implicit val M: Monad[F] = wvlet.airframe.bind[Monad[F]]

}
