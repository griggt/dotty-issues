
def barf(resource: Resource[List]): Unit = {
  for {
    res <-resource.allocated
  } yield ()
}

trait Resource[F[_]] {
  def allocated[G[x] >: F[x]]: G[Int]
}
