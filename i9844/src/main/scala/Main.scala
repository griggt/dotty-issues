trait Foo[A]

class Baz[A]  {
  trait Bar {
    this: Foo[A] =>
    def bar(a: A): Unit
  }
}
