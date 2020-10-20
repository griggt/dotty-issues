package i8847.lib

import scala.util.parsing.input._
import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec
import scala.language.implicitConversions

trait Parsers {
  type Elem
  type Input = Reader[Elem]

  sealed abstract class ParseResult[+T] {
    def map[U](f: T => U): ParseResult[U]
    def mapPartial[U](f: PartialFunction[T, U], error: T => String): ParseResult[U]
    def flatMapWithNext[U](f: T => Input => ParseResult[U]): ParseResult[U]
    def filterWithError(p: T => Boolean, error: T => String, position: Input): ParseResult[T]
    def append[U >: T](a: => ParseResult[U]): ParseResult[U]
    def isEmpty: Boolean = ???
    def get: T
    def getOrElse[B >: T](default: => B): B = ???

    val next: Input
    val successful: Boolean
  }

  case class Success[+T](result: T, override val next: Input) extends ParseResult[T] {
    def map[U](f: T => U) = Success(f(result), next)
    def mapPartial[U](f: PartialFunction[T, U], error: T => String): ParseResult[U]
       = if(f.isDefinedAt(result)) Success(f(result), next)
         else Failure(error(result), next)

    def flatMapWithNext[U](f: T => Input => ParseResult[U]): ParseResult[U]
      = f(result)(next)

    def filterWithError(p: T => Boolean, error: T => String, position: Input): ParseResult[T] =
      if (p(result)) this
      else Failure(error(result), position)

    def append[U >: T](a: => ParseResult[U]): ParseResult[U] = this

    def get: T = result

    /** The toString method of a Success. */
    override def toString = s"[${next.pos}] parsed: $result"

    val successful = true
  }

  sealed abstract class NoSuccess(val msg: String, override val next: Input) extends ParseResult[Nothing] { // when we don't care about the difference between Failure and Error
    val successful = false

    def map[U](f: Nothing => U) = this
    def mapPartial[U](f: PartialFunction[Nothing, U], error: Nothing => String): ParseResult[U] = this

    def flatMapWithNext[U](f: Nothing => Input => ParseResult[U]): ParseResult[U]
      = this

    def filterWithError(p: Nothing => Boolean, error: Nothing => String, position: Input): ParseResult[Nothing] = this

    def get: Nothing = scala.sys.error("No result when parsing failed")
  }
  object NoSuccess {
    def unapply[T](x: ParseResult[T]) = x match {
      case Failure(msg, next)   => Some((msg, next))
      case Error(msg, next)     => Some((msg, next))
      case _                    => None
    }
  }

  case class Failure(override val msg: String, override val next: Input) extends NoSuccess(msg, next) {
    /** The toString method of a Failure yields an error message. */
    override def toString = s"[${next.pos}] failure: $msg\n\n${next.pos.longString}"

    def append[U >: Nothing](a: => ParseResult[U]): ParseResult[U] = { val alt = a; alt match {
      case Success(_, _) => alt
      case ns: NoSuccess => if (alt.next.pos < next.pos) this else alt
    }}
  }

  case class Error(override val msg: String, override val next: Input) extends NoSuccess(msg, next) {
    /** The toString method of an Error yields an error message. */
    override def toString = s"[${next.pos}] error: $msg\n\n${next.pos.longString}"
    def append[U >: Nothing](a: => ParseResult[U]): ParseResult[U] = this
  }

  def Parser[T](f: Input => ParseResult[T]): Parser[T] = ???

  def OnceParser[T](f: Input => ParseResult[T]): Parser[T] with OnceParser[T] = ???

  abstract class Parser[+T] extends (Input => ParseResult[T]) {
    private var name: String = ""
    def named(n: String): this.type = ???
    override def toString = s"Parser ($name)"

    def apply(in: Input): ParseResult[T]
    def flatMap[U](f: T => Parser[U]): Parser[U] = ???
    def map[U](f: T => U): Parser[U] = ???
    def filter(p: T => Boolean): Parser[T] = ???
    def withFilter(p: T => Boolean): Parser[T] = ???

    def append[U >: T](p0: => Parser[U]): Parser[U] = ???

    def ~ [U](q: => Parser[U]): Parser[~[T, U]] = ???
    def ~> [U](q: => Parser[U]): Parser[U] = ???
    def <~ [U](q: => Parser[U]): Parser[T] = ???
    def - [U](q: Parser[U]): Parser[T] = ???
    def ~! [U](p: => Parser[U]): Parser[~[T, U]] = ???
    def ~>! [U](q: => Parser[U]): Parser[U] = ???
    def <~! [U](q: => Parser[U]): Parser[T] = ???
    def | [U >: T](q: => Parser[U]): Parser[U] = ???
    def ||| [U >: T](q0: => Parser[U]): Parser[U] = ???
    def ^^ [U](f: T => U): Parser[U] = ???
    def ^^^ [U](v: => U): Parser[U] = ???
    def ^? [U](f: PartialFunction[T, U], error: T => String): Parser[U] = ???
    def ^? [U](f: PartialFunction[T, U]): Parser[U] = ???
    def into[U](fq: T => Parser[U]): Parser[U] = ???

    def >>[U](fq: T => Parser[U]) = ???
    def * = ???
    def *[U >: T](sep: => Parser[(U, U) => U]) = ???
    def + = ???
    def ? = ???
    def withFailureMessage(msg: String) = ???
    def withErrorMessage(msg: String) = ???
  }

  def commit[T](p: => Parser[T]) = ???
  def elem(kind: String, p: Elem => Boolean) = ???
  def elem(e: Elem): Parser[Elem] = ???

  implicit def accept(e: Elem): Parser[Elem] = ???

  def accept[ES](es: ES)(implicit f: ES => List[Elem]): Parser[List[Elem]] = ???
  def accept[U](expected: String, f: PartialFunction[Elem, U]): Parser[U] = ???

  def acceptIf(p: Elem => Boolean)(err: Elem => String): Parser[Elem] = ???
  def acceptMatch[U](expected: String, f: PartialFunction[Elem, U]): Parser[U] = ???
  def acceptSeq[ES](es: ES)(implicit f: ES => Iterable[Elem]): Parser[List[Elem]] = ???

  def failure(msg: String) = ???
  def err(msg: String) = ???
  def success[T](v: T) = ???
  def log[T](p: => Parser[T])(name: String): Parser[T] = ???
  def rep[T](p: => Parser[T]): Parser[List[T]] = ???
  def repsep[T](p: => Parser[T], q: => Parser[Any]): Parser[List[T]] = ???
  def rep1[T](p: => Parser[T]): Parser[List[T]] = ???

  def rep1[T](first: => Parser[T], p0: => Parser[T]): Parser[List[T]] = ???
  def repN[T](num: Int, p: => Parser[T]): Parser[List[T]] = ???
  def repNM[T](n: Int, m: Int, p: Parser[T], sep: Parser[Any] = success(())): Parser[List[T]] = ???
  def rep1sep[T](p : => Parser[T], q : => Parser[Any]): Parser[List[T]] = ???
  def chainl1[T](p: => Parser[T], q: => Parser[(T, T) => T]): Parser[T] = ???
  def chainl1[T, U](first: => Parser[T], p: => Parser[U], q: => Parser[(T, U) => T]): Parser[T] = ???
  def chainr1[T, U](p: => Parser[T], q: => Parser[(T, U) => U], combine: (T, U) => U, first: U): Parser[U] = ???

  def opt[T](p: => Parser[T]): Parser[Option[T]] = ???
  def not[T](p: => Parser[T]): Parser[Unit] = ???
  def guard[T](p: => Parser[T]): Parser[T] = ???
  def positioned[T <: Positional](p: => Parser[T]): Parser[T] = ???
  def phrase[T](p: Parser[T]): Parser[T] = ???
  def mkList[T] = ???

  case class ~[+a, +b](_1: a, _2: b) {
    override def toString = ""
  }

  trait OnceParser[+T] extends Parser[T] {
    override def ~ [U](p: => Parser[U]): Parser[~[T, U]] = ???
    override def ~> [U](p: => Parser[U]): Parser[U] = ???
    override def <~ [U](p: => Parser[U]): Parser[T] = ???
  }
}
