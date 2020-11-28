import scala.compiletime.testing._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ABug4Spec extends AnyFunSuite with Matchers {
  test("foo") {
    typeChecks("") shouldBe true
  }
}

