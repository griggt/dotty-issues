class F

object Test {
  def test(): Unit = {
    val fooTag = BoomInspect.inspect[F]
  }
}
