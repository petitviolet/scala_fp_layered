package net.petitviolet.example.queries

import cats.syntax.{ FlatMapSyntax, FunctorSyntax }

trait Query[M[_]] extends FunctorSyntax with FlatMapSyntax {}
