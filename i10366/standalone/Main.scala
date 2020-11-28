import scala.quoted._

case class Position(fileName: String, lineNumber: Int)

object Position:
  implicit inline def here: Position = ${ genPosition }
  private def genPosition(using QuoteContext): Expr[Position] = ???

object Matchers:
  class AnyShouldWrapper[T](val lhs: T, val pos: Position):
    def shouldBe(right: Any): Unit = ???

  implicit def convertToAnyShouldWrapper[T](o: T)(implicit pos: Position): AnyShouldWrapper[T] =
    AnyShouldWrapper(o, pos)
