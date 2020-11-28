import scala.compiletime.testing._

trait Position

object Test:
  class ShouldWrapper(lhs: Boolean):
    def shouldBe(rhs: Boolean): Unit = ???

  implicit inline def here: Position = ???
  implicit def convertToShouldWrapper(o: Boolean)(implicit pos: Position): ShouldWrapper = ???

  def test(): Unit =
    typeChecks("class Foo") shouldBe true
