trait Foo[+A] {
  def foo[A1 >: A](a: A1): Foo[A1]
}

trait Bar[-B]

trait X {
  def bla[A](m: Bar[A]): Foo[A]

  def baz(x: Bar[Int]): Unit = {
    var t = bla(x)
    t = t.foo(0)
  }
}
