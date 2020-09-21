trait Foo[A]

trait Baz[A]  {
  trait Bar {
    this: Foo[A] =>
    def bar(a: A): Unit
  }
}
