import scala.compiletime._
import scala.compiletime.testing._
import org.scalatest.matchers.should.Matchers._

class Test {
  def x = typeChecks("trait Foo") shouldBe true  // BAD
  //def x = typeCheckErrors("trait Foo") shouldBe Nil  // BAD
  //def x = constValue[1] shouldBe 1  // this is OK
  //def x = summonInline shouldBe 1  // this is OK
}
