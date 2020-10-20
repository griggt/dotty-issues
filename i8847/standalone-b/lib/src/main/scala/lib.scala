package i8847b.lib

trait Foo {
  def foo(): Unit = ???
}

trait Bar extends Foo {
  override def foo(): Unit = super.foo()
}

trait Baz extends Foo {
  override def foo(): Unit = super.foo()
}
