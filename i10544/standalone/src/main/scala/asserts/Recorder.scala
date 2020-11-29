package verify
package asserts

abstract class Recorder[A, R] {
  inline def apply(value: A): R =
    ${ RecorderMacro.apply('value) }
}

case class Recording[A](recordedExprs: List[RecordedExpression[A]])
case class RecordedValue(value: Any, anchor: Int)
case class RecordedExpression[T](text: String, ast: String, value: T, recordedValues: List[RecordedValue])

// one instance per recording
class RecorderRuntime[A, R] {
  def resetValues(): Unit = ???
  def recordValue[U](value: U, anchor: Int): U = ???
  def recordMessage(message: => String): Unit = ???
  def recordExpression(text: String, ast: String, value: A): Unit = ???
  def completeRecording(): R = ???
}

class PowerAssert extends Recorder[Boolean, Unit]

object PowerAssert {
  def assert: PowerAssert = new PowerAssert()
}
