import scala.compiletime.testing._

trait Foo

object Test:
  class ShouldWrapper(lhs: Boolean):
    def shouldBe(rhs: Boolean): Unit = ???

  implicit val foo: Foo = ???
  implicit def convertToShouldWrapper(x: Boolean)(implicit f: Foo): ShouldWrapper = ???

  def test(): Unit =
    typeChecks("class Bar") shouldBe true
