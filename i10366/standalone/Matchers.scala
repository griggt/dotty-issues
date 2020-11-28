import scala.language.implicitConversions

object Matchers {
  class AnyShouldWrapper[T](val lhs: T, val pos: Position) {
    def shouldBe(right: Any): Unit = ???
  }

  implicit def convertToAnyShouldWrapper[T](o: T)(implicit pos: Position): AnyShouldWrapper[T] =
    AnyShouldWrapper(o, pos)
}
