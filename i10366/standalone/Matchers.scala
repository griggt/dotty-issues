import scala.language.implicitConversions
import org.scalactic.source

object Matchers {
  class AnyShouldWrapper[T](val lhs: T, val pos: source.Position) {
    def shouldBe(right: Any): Unit = ???
  }

  implicit def convertToAnyShouldWrapper[T](o: T)(implicit pos: source.Position): AnyShouldWrapper[T] =
    AnyShouldWrapper(o, pos)
}
