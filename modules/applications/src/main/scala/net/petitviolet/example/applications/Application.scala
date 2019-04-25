package net.petitviolet.example.applications

import cats.syntax.{FlatMapSyntax, FunctorSyntax}

trait Application[F[_]] extends FunctorSyntax with FlatMapSyntax {}
