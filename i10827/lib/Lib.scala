package test.http4s

class Method(name: String)

object Method {
  trait Safe
  trait PermitsBody extends Method

  type SafeMethodWithBody = Method with Safe with PermitsBody
  val GET: SafeMethodWithBody = new Method("GET") with Safe with PermitsBody
}

trait Methods {
  val GET: Method.GET.type = Method.GET
}
