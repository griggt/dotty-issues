
def barf: Unit = {
  val resource: Resource[List] = ???
  for {
    res <- resource.allocated
  } yield ()
}

class Resource[F[_]] {
  def allocated[G[x] >: F[x]]: G[(Unit, G[Unit])] = ???
}
