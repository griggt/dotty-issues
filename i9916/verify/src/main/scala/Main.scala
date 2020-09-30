import com.twitter.finagle.liveness.FailureAccrualFactory
import com.twitter.finagle.filter._
import com.twitter.finagle.client._

trait A {
  def p: FailureAccrualFactory.Param2
//  val p = FailureAccrualFactory.Param(0, d)
//  val t = Stack.Param[FailureAccrualFactory.Param](p)
}
