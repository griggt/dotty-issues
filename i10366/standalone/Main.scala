import scala.quoted._

trait Position

object Position:
  implicit inline def here: Position = ${ genPosition }
  def genPosition(using Quotes): Expr[Position] = ???

object Matchers:
  class AnyShouldWrapper[T](val lhs: T, val pos: Position):
    def shouldBe(right: Any): Unit = ???

  implicit def convertToAnyShouldWrapper[T](o: T)(implicit pos: Position): AnyShouldWrapper[T] =
    AnyShouldWrapper(o, pos)
