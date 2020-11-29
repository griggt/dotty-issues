import verify._

object Test extends Assertion {
  def foo(x: Int): String = "foo"
  def bar(): Unit = assert(foo(0) == "foo")
}
