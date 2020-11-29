import verify.asserts.PowerAssert

object Test {
  def foo(x: Int): String = "foo"
  def bar(): Unit = {
    val x = foo(0)
    PowerAssert.assert(foo(0) == "foo")
  }
}
