import cats.effect.{Resource, IO}

def barf: Unit = {
  val resource: Resource[IO, Unit] = ???
  for {
    res <- resource.allocated
  } yield ()
}
