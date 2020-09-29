
class PowerAssert extends Recorder[Boolean, Unit] {
  //val failEarly: Boolean = true

  /*
  class AssertListener extends RecorderListener[Boolean, Unit] {
    override def expressionRecorded(recordedExpr: RecordedExpression[Boolean], recordedMessage: Function0[String]): Unit = {
      lazy val rendering: String = new ExpressionRenderer(showTypes = false, shortString = false).render(recordedExpr)
      if (!recordedExpr.value && failEarly) {
        val msg = recordedMessage()
        val header =
          "assertion failed" +
            (if (msg == "") ""
             else ": " + msg)
        throw new AssertionError(header + "\n\n" + rendering)
      }
    }

    override def recordingCompleted(recording: Recording[Boolean], recordedMessage: Function0[String]) = {}
  }
  */

  //override lazy val listener: RecorderListener[Boolean, Unit] = ??? //new AssertListener
}

/*
object PowerAssert {
  lazy val assert: PowerAssert = ???
  lazy val stringAssertEqualsListener: RecorderListener[String, Unit] = ???
}
*/