package test.http4s

case class Method(name: String)

object Method {
  sealed trait Semantics
  object Semantics {
    trait Safe extends Semantics
  }

  sealed trait PermitsBody extends Method

  import Semantics._

  type SafeMethodWithBody = Method with Safe with PermitsBody

  val GET: SafeMethodWithBody = new Method("GET") with Safe with PermitsBody
}

trait Methods {
  val GET: Method.GET.type = Method.GET
}
