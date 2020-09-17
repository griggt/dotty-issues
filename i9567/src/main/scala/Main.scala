
trait Foo[F[_]] {
  def foo[G[x] >: F[x]]: G[Int]
}

def bar(x: Foo[List]): Unit = {
  for {
    _ <- x.foo
  } yield ()
}
