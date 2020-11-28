import scala.compiletime.testing._

object Test:
  import Matchers._
  typeChecks("class Foo") shouldBe true
