import cats.MonadError


implicit def listBracket: Bracket[List, Throwable] = ???

def barf: Unit = {
  val resource: Resource[List, Unit] = ???
  for {
    res <- resource.allocated
  } yield ()
}

trait Bracket[F[_], E] extends MonadError[F, E]

sealed abstract class Resource[+F[_], +A] {
  def allocated[G[x] >: F[x], B >: A](implicit F: Bracket[G, Throwable]): G[(B, G[Unit])] = ???
}
