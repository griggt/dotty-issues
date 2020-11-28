import scala.compiletime.testing._
import should.Matchers._

class Test {
  def x = typeChecks("trait Foo") shouldBe true
}
