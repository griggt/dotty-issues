package should

import scala.language.implicitConversions

import org.scalactic.source
import org.scalactic.Prettifier
import org.scalatest.Assertion

trait Matchers {
  sealed class AnyShouldWrapper[T](val leftSideValue: T, val pos: source.Position, val prettifier: Prettifier) {
    def shouldBe(right: Any): Assertion = ???
  }

  implicit def convertToAnyShouldWrapper[T](o: T)(implicit pos: source.Position, prettifier: Prettifier): AnyShouldWrapper[T] =
    new AnyShouldWrapper(o, pos, prettifier)
}

object Matchers extends Matchers
