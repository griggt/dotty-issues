trait BasicTestSuite extends Assertion {
  def test(name: String)(f: => Unit): Unit = ???
}
