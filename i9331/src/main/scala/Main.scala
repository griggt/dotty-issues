import scala.util.Try


object Main {

  //val a: PartialFunction[Any => Any, Any] = (f => Try(f(0)) match { case v => v })  // error
  //val a = ((f: Any => Any) => Try(f(0)) match { case v => v })                      // OK, val a: (Any => Any) => Try[Any]
  //val a = ((f: Int => Int) => Try(f(0)) match { case v => v })                      // OK, val a: (Int => Int) => Try[Int]
  //val a: PartialFunction[Int => Int, Try[Int]] = ((f: Int => Int) => Try(f(0)) match { case v => v })  // error

  // Found:    (Main.a : (Int => Int) => Try[Int])
  // Required: PartialFunction[Int => Int, Any]
  // val a = ((f: Int => Int) => Try(f(0)) match { case v => v })
  // val b: PartialFunction[Int => Int, Any] = a


  def g(x: Any): Any = x
  def h(x: => Any): Any = x

  object H {
    def apply(x: Any): Any = ???
  }

  object HT {
    def apply[T](x: T): Any = ???
  }

  object HT0 {
    def apply[T](x: => T): Any = ???
  }

  object H0 {
    def apply(x: => Any): Any = ???
  }


  //val a: PartialFunction[Any => Any, Any] = (f => Try(f(0)) match { case v => v })  // error
  //val a: PartialFunction[Any => Any, Any] = (f => f(0) match { case v => v })  // OK
  //val a: PartialFunction[Any => Any, Any] = (f => false match { case v => v })  // OK
  //val a: PartialFunction[Any => Any, Any] = (f => Try(false) match { case v => v })  // OK
  //val a: PartialFunction[() => Any, Any]  = (f => Try(f()) match { case v => v })   // OK
  //val a: PartialFunction[Any => Any, Any] = (f => g(f(0)) match { case v => v })  // OK
  //val a: PartialFunction[Any => Any, Any] = (f => H(f(0)) match { case v => v }) // OK
  //val a: PartialFunction[Any => Any, Any] = (f => HT0(f(0)) match { case v => v })   // error
  //val a: PartialFunction[Any => Any, Any] = (f => H0(f(0)) match { case v => v })  // error
  val a: PartialFunction[Any => Any, Any] = (f => h(f(0)) match { case v => v })  // error
}
