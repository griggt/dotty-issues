import scala.concurrent.{ ExecutionContext, Future }

trait BasicTestSuite extends AbstractTestSuite with Assertion {
  private[this] implicit lazy val ec: ExecutionContext = executionContext

  def test(name: String)(f: => Void): Unit =
    synchronized {
      if (isInitialized) throw initError()
      propertiesSeq = propertiesSeq :+ TestSpec.sync[Unit](name, _ => f)
    }

  def testAsync(name: String)(f: => Future[Unit]): Unit =
    synchronized {
      if (isInitialized) throw initError()
      propertiesSeq = propertiesSeq :+ TestSpec.async[Unit](name, _ => f)
    }

  lazy val properties: Properties[_] =
    synchronized {
      if (!isInitialized) isInitialized = true
      Properties[Unit](() => (), _ => Void.UnitRef, () => (), () => (), propertiesSeq)
    }

  def executionContext: ExecutionContext = platform.defaultExecutionContext

  private[this] var propertiesSeq = Seq.empty[TestSpec[Unit, Unit]]
  private[this] var isInitialized = false

  private[this] def initError() =
    new AssertionError(
      "Cannot define new tests after BasicTestSuite was initialized"
    )
}
