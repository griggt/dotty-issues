
implicit def listBracket: Bracket[List] = ???

def barf: Unit = {
  val resource: Resource[List, Unit] = ???
  for {
    res <- resource.allocated
  } yield ()
}

trait Bracket[F[_]]

sealed abstract class Resource[F[_], A] {
  def allocated[G[x] >: F[x], B >: A](implicit F: Bracket[G]): G[(B, G[Unit])] = ???
}
