import scala.quoted._

case class Position(fileName: String, lineNumber: Int)

object Position {
  implicit inline def here: Position = ${ genPosition }
  private def genPosition(using QuoteContext): Expr[Position] = ???
}
