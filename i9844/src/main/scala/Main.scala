trait Foo[A]

trait Baz[A]  {
  type Q = A
  trait Bar {
    this: Foo[A] =>
    def bar(a: Q): Unit
  }
}
