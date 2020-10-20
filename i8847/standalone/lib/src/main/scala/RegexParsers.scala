package i8847.lib

import scala.util.matching.Regex
import scala.util.parsing.input._
import scala.language.implicitConversions

trait RegexParsers extends Parsers {

  type Elem = Char

  protected val whiteSpace = """\s+""".r

  def skipWhitespace = ???

  protected def handleWhiteSpace(source: java.lang.CharSequence, offset: Int): Int = ???

  implicit def literal(s: String): Parser[String] = ???
  implicit def regex(r: Regex): Parser[String] = ???

  override def positioned[T <: Positional](p: => Parser[T]): Parser[T] = ???
  private def ws[T](p: Parser[T]): Parser[T] = ???

  override def err(msg: String) = ???
  override def phrase[T](p: Parser[T]): Parser[T] = ???

  def parse[T](p: Parser[T], in: Reader[Char]): ParseResult[T] = ???
  def parse[T](p: Parser[T], in: java.lang.CharSequence): ParseResult[T] = ???
  def parse[T](p: Parser[T], in: java.io.Reader): ParseResult[T] = ???

  def parseAll[T](p: Parser[T], in: Reader[Char]): ParseResult[T] = ???
  def parseAll[T](p: Parser[T], in: java.io.Reader): ParseResult[T] = ???
  def parseAll[T](p: Parser[T], in: java.lang.CharSequence): ParseResult[T] = ???
}
