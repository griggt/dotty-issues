trait Foo
object Foo {
  trait Bar[A]
}

class Baz[A] extends Foo {
  trait BarBar {
    this: Foo.Bar[A] =>
    def bar(a: A): Unit
  }
}
