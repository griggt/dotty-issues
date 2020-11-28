import scala.compiletime.testing._

trait Position

object Test:
  class AnyShouldWrapper[T](val lhs: T, val pos: Position):
    def shouldBe(right: Any): Unit = ???

  implicit inline def here: Position = ???

  implicit def convertToAnyShouldWrapper[T](o: T)(implicit pos: Position): AnyShouldWrapper[T] =
    AnyShouldWrapper(o, pos)

  def test(): Unit =
    typeChecks("class Foo") shouldBe true
