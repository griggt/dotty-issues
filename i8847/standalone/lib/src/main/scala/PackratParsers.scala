package i8847.lib

import scala.util.parsing.input.Position

trait PackratParsers extends Parsers {
  private case class Head(var headParser: Parser[_], var involvedSet: List[Parser[_]], var evalSet: List[Parser[_]])
}
