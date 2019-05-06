package net.petitviolet.example.queries

import cats.syntax.{ FlatMapSyntax, FunctorSyntax }

trait Query[F[_]] extends FunctorSyntax with FlatMapSyntax {}
