object Test {
  def foo(x: Int): String = "foo"
  def bar(): Unit = PowerAssert.assert(foo(0) == "foo")
}
