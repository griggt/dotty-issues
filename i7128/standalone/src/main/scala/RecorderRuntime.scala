class RecorderRuntime[A, R] {
  //def resetValues(): Unit = ???
  def recordValue[U](value: U, anchor: Int): U = ???
  //def recordMessage(message: => String): Unit = ???
  //def recordExpression(text: String, ast: String, value: A): Unit = ???
  //def completeRecording(): R = ???
}
