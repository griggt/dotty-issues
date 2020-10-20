package i8847.lib

import scala.util.parsing.input.{ Reader, Position }
import scala.collection.mutable
import scala.language.implicitConversions

trait PackratParsers extends Parsers {
  class PackratReader[+T](underlying: Reader[T]) extends Reader[T] { outer =>

    private[PackratParsers] val cache = ???
    private[PackratParsers] def getFromCache[T](p: Parser[T]): Option[MemoEntry[T]] = ???
    private[PackratParsers] def updateCacheAndGet[T](p: Parser[T], w: MemoEntry[T]): MemoEntry[T] = ???
    private[PackratParsers] val recursionHeads: mutable.HashMap[Position, Head] = ???
    private[PackratParsers] var lrStack: List[LR] = ???

    override def source: java.lang.CharSequence = ???
    override def offset: Int = ???

    def first: T = ???
    def rest: Reader[T] = ???

    def pos: Position = ???
    def atEnd: Boolean = ???
  }

  override def phrase[T](p: Parser[T]) = ???

  private def getPosFromResult(r: ParseResult[_]): Position = ???

  private case class MemoEntry[+T](var r: Either[LR,ParseResult[_]]) {
    def getResult: ParseResult[T] = ???
  }

  private case class LR(var seed: ParseResult[_], var rule: Parser[_], var head: Option[Head]) {
    def getPos: Position = ???
  }

  private case class Head(var headParser: Parser[_], var involvedSet: List[Parser[_]], var evalSet: List[Parser[_]]) {
    def getHead = ???
  }

  abstract class PackratParser[+T] extends super.Parser[T]

  implicit def parser2packrat[T](p: => super.Parser[T]): PackratParser[T] = ???

  private def recall(p: super.Parser[_], in: PackratReader[Elem]): Option[MemoEntry[_]] = ???
  private def setupLR(p: Parser[_], in: PackratReader[_], recDetect: LR): Unit = ???
  private def lrAnswer[T](p: Parser[T], in: PackratReader[Elem], growable: LR): ParseResult[T] = ???

  def memo[T](p: super.Parser[T]): PackratParser[T] = ???

  private def grow[T](p: super.Parser[T], rest: PackratReader[Elem], head: Head): ParseResult[T] = ???
}
