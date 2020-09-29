object N
type T[A] >: N.type


/*
def foo[A](x: T[A]): Boolean = ???

object Test extends Assertion {
  def boom = assert(foo(N))
}
*/


object Test extends BasicTestSuite {
  test("") {
    def foo(x: T[Int]): Boolean = ???
    def boom = assert(foo(N))
  }
}
