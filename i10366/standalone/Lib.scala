import scala.compiletime.testing._

trait Position

object Test:
  class AnyShouldWrapper[T](val lhs: T):
    def shouldBe(right: Any): Unit = ???

  implicit inline def here: Position = ???
  implicit def convertToAnyShouldWrapper[T](o: T)(implicit pos: Position): AnyShouldWrapper[T] = ???

  def test(): Unit =
    typeChecks("class Foo") shouldBe true
