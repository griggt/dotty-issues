package test.http4s

import test.http4s.Method.Semantics

case class Method (name: String) extends Semantics

object Method {
  sealed trait Semantics
  object Semantics {
    trait Safe extends Semantics
  }

  sealed trait PermitsBody extends Method

  import Semantics._

  type SafeMethod = Method with Safe
  type SafeMethodWithBody = Method with Safe /*SafeMethod*/ with PermitsBody

  val GET: SafeMethodWithBody = new Method("GET") with Safe with PermitsBody
}

trait Methods {
  val GET: Method.GET.type = Method.GET
}
