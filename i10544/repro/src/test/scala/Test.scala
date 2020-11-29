import verify._

object Test extends BasicTestSuite {
  def foo(x: Int): String = "foo"
  def bar(): Unit = assert(foo(0) == "foo")
}
