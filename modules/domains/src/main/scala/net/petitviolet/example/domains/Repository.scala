package net.petitviolet.example.domains

trait Repository[F[_], E <: Entity] {
  def findById(id: Id[E]): F[Option[E]]

  def store(entity: E): F[E]
}

trait RepositoryResolver[Repo[F[_]] <: Repository[F, _]] {
  @inline def apply[F[_]: Repo]: Repo[F] = implicitly
}
