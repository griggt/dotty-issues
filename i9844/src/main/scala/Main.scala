trait Foo[A] {
  def test(x: A, y: A): Boolean
}

trait Baz[A]  {
  type Q = A
  val baz: A

  trait Bar {
    this: Foo[A] =>
    def bar(a: Q): Unit = {
      test(a, baz)
    }
  }
}
