package net.petitviolet.example.domains

import scalaz.Kleisli
import scalikejdbc.DBSession

import scala.concurrent.{ExecutionContext, Future}

package object impl {
  private[impl] type Ctx = (DBSession, ExecutionContext)
  type AsyncIO[A] = Kleisli[Future, Ctx, A]

  private[impl] implicit def _dbSession(implicit ctx: Ctx): DBSession = ctx._1
  private[impl] implicit def _ec(implicit ctx: Ctx): ExecutionContext = ctx._2
}
