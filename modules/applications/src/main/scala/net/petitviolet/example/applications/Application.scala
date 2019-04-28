package net.petitviolet.example.applications

import cats.syntax.{ FlatMapSyntax, FunctorSyntax }

trait Application[M[_]] extends FunctorSyntax with FlatMapSyntax {}
