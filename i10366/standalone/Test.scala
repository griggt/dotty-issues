import scala.compiletime.testing._

object Test {
  import Matchers._
  typeChecks("trait Foo") shouldBe true
}
