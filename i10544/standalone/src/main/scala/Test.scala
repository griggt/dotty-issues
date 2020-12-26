// Test.scala
object Test {
  def foo(x: Int): String = "foo"
  def bar(): Unit = PowerAssert.assert(foo(0) == "foo")
}

/*
object N
type T[A] >: N.type

object Test {
  def foo(x: T[Int]): Boolean = ???
  def boom = PowerAssert.assert(foo(N))
}
 */
