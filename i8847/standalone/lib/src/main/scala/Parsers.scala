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
    def map[U](f: T => U) = ???
    def mapPartial[U](f: PartialFunction[T, U], error: T => String): ParseResult[U] = ???
    def flatMapWithNext[U](f: T => Input => ParseResult[U]): ParseResult[U] = ???
    def filterWithError(p: T => Boolean, error: T => String, position: Input): ParseResult[T] = ???
    def append[U >: T](a: => ParseResult[U]): ParseResult[U] = ???
    def get: T = ???
    override def toString = ""
    val successful = true
  }

  sealed abstract class NoSuccess(val msg: String, override val next: Input) extends ParseResult[Nothing] { // when we don't care about the difference between Failure and Error
    val successful = false
    def map[U](f: Nothing => U) = this
    def mapPartial[U](f: PartialFunction[Nothing, U], error: Nothing => String): ParseResult[U] = ???
    def flatMapWithNext[U](f: Nothing => Input => ParseResult[U]): ParseResult[U] = ???
    def filterWithError(p: Nothing => Boolean, error: Nothing => String, position: Input): ParseResult[Nothing] = ???
    def get: Nothing = ???
  }

  object NoSuccess {
    def unapply[T](x: ParseResult[T]) = ???
  }

  case class Failure(override val msg: String, override val next: Input) extends NoSuccess(msg, next) {
    override def toString = ""
    def append[U >: Nothing](a: => ParseResult[U]): ParseResult[U] = ???
  }

  case class Error(override val msg: String, override val next: Input) extends NoSuccess(msg, next) {
    override def toString = ""
    def append[U >: Nothing](a: => ParseResult[U]): ParseResult[U] = ???
  }

  def Parser[T](f: Input => ParseResult[T]): Parser[T] = ???

//  def OnceParser[T](f: Input => ParseResult[T]): Parser[T] with OnceParser[T] = ???

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
  }

  def commit[T](p: => Parser[T]) = ???
  def elem(kind: String, p: Elem => Boolean) = ???
  def elem(e: Elem): Parser[Elem] = ???

  def failure(msg: String) = ???
  def err(msg: String) = ???
  def success[T](v: T) = ???
  def log[T](p: => Parser[T])(name: String): Parser[T] = ???
  def rep[T](p: => Parser[T]): Parser[List[T]] = ???
  def repsep[T](p: => Parser[T], q: => Parser[Any]): Parser[List[T]] = ???
  def rep1[T](p: => Parser[T]): Parser[List[T]] = ???

  def opt[T](p: => Parser[T]): Parser[Option[T]] = ???
  def not[T](p: => Parser[T]): Parser[Unit] = ???
  def guard[T](p: => Parser[T]): Parser[T] = ???
  def positioned[T <: Positional](p: => Parser[T]): Parser[T] = ???
  def phrase[T](p: Parser[T]): Parser[T] = ???
  def mkList[T] = ???

//  trait OnceParser[+T] extends Parser[T]
}
