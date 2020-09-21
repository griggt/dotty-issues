trait Foo[+A] {
  def foo[A1 >: A](a: A1): A
}

trait Bar[-C]

trait X {
  def bla[A](m: Bar[A]): Foo[A]
  def baz(x: Bar[Int]): Int = bla(x).foo(2) + 1
}
