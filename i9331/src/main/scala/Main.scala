object Test {
def g(x: => Any): Any = x
def h(x: Any): Any = x

val a: PartialFunction[Any => Any, Any] = (f => g(f(0)) match { case v => v })  // error
//val a: PartialFunction[Any => Any, Any] = (f => h(f(0)) match { case v => v })  // OK
}