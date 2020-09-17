trait Foo[F[_]] {
  def foo[G[x] >: F[x]]: G[Unit]
}

trait M[A] {
  def flatMap[B](f: A => M[B]): M[B]
  def map[B](f: A => B): M[B]
}

def bar(x: Foo[M]): Unit = {
  val a = x.foo
  for {
    _ <- a
  } yield ()
}
