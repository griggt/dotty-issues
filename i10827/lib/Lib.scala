package test.http4s

import test.http4s.Method.Semantics

sealed abstract case class Method private (name: String) extends Semantics {}

object Method {
  sealed trait Semantics {
    def isIdempotent: Boolean
    def isSafe: Boolean
  }

  object Semantics {
    trait Safe extends Semantics {
      def isIdempotent: Boolean = true
      def isSafe: Boolean = true
    }
  }

  sealed trait PermitsBody extends Method

  import Semantics._

  type SafeMethod = Method with Safe
  type SafeMethodWithBody = SafeMethod with PermitsBody

  val GET: SafeMethodWithBody = new Method("GET") with Safe with PermitsBody
}

trait Methods {
  val GET: Method.GET.type = Method.GET
}
