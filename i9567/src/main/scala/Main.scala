
def barf: Unit = {
  val resource: Resource[List, Unit] = ???
  for {
    res <- resource.allocated
  } yield ()
}

abstract class Resource[F[_], A] {
  def allocated[G[x] >: F[x]]: G[(A, G[Unit])] = ???
}
