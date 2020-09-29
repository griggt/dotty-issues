
// one instance per recording
class RecorderRuntime[A, R](listener: RecorderListener[A, R]) {
  protected var recordedValues: List[RecordedValue] = List.empty
  protected var recordedExprs: List[RecordedExpression[A]] = List.empty
  protected var recordedMessage: Function0[String] = () => ""

  def resetValues(): Unit = {
    recordedValues = List.empty
  }

  def recordValue[U](value: U, anchor: Int): U = {
    val recordedValue = RecordedValue(value, anchor)
    listener.valueRecorded(recordedValue)
    recordedValues = recordedValue :: recordedValues
    value
  }

  def recordMessage(message: => String): Unit = {
    recordedMessage = () => message
  }

  def recordExpression(text: String, ast: String, value: A): Unit = {
    // recordedValues.reverse causes <function2> to slip in
    val recordedExpr = RecordedExpression(text, ast, value, recordedValues)
    resetValues()
    listener.expressionRecorded(recordedExpr, recordedMessage)
    recordedExprs = recordedExpr :: recordedExprs
  }

  def completeRecording(): R = {
    val recording = Recording(recordedExprs.reverse)
    val msg = recordedMessage
    listener.recordingCompleted(recording, msg)
  }
}
