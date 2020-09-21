trait Foo[+A] {
  def foo[A1 >: A](a: A1): Foo[A1]
}

trait Bar[-C]

trait X {
  def bla[A](m: Bar[A]): Foo[A]
  def baz(x: Bar[Int]): Foo[Int] = {
    var t = bla(x)
    t = t.foo(1)
    return t
  }
}
