import cats.effect.{Resource, Bracket}

implicit def listBracket: Bracket[List, Throwable] = ???

def barf: Unit = {
  val resource: Resource[List, Unit] = ???
  for {
    res <- resource.allocated
  } yield ()
}
