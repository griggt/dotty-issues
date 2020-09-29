import scala.concurrent.{ ExecutionContext, Future }

trait BasicTestSuite extends AbstractTestSuite with Assertion {
  private[this] implicit lazy val ec: ExecutionContext = ???

  def test(name: String)(f: => Void): Unit = ???
  def testAsync(name: String)(f: => Future[Unit]): Unit = ???

  lazy val properties: Properties[_] = ???
  def executionContext: ExecutionContext = ???

  private[this] var propertiesSeq = ???
  private[this] var isInitialized = false
  private[this] def initError() = ???
}
