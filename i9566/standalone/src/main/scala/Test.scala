trait Applicative[F[_]]

trait IO[+A]

object IO {
  def apply[A](thunk: => A): IO[A] = ???
  implicit def applicativeForIO: Applicative[IO] = ???
}

sealed abstract class Resource[+F[_], +A] {
  def map[G[x] >: F[x], B](f: A => B)(implicit F: Applicative[G[*]]): Resource[G[*], B] = ???
}

object Resource {
  def liftF[F[_], A](fa: F[A])(implicit F: Applicative[F]): Resource[F, A] = ???
}

object Test {
  def test = for {
    _ <- Resource.liftF(IO {})
  } yield ()
}
