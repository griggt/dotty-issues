package verify
package asserts

abstract class Recorder[A] {
  inline def apply(value: A): Unit = ${ RecorderMacro.apply('value) }
}

case class Recording[A](recordedExprs: List[RecordedExpression[A]])
case class RecordedValue(value: Any, anchor: Int)
case class RecordedExpression[T](text: String, ast: String, value: T, recordedValues: List[RecordedValue])

// one instance per recording
class RecorderRuntime[A] {
  def resetValues(): Unit = ???
  def recordValue[U](value: U, anchor: Int): U = ???
  def recordExpression(text: String, ast: String, value: A): Unit = ???
}

class PowerAssert extends Recorder[Boolean]

object PowerAssert {
  def assert: PowerAssert = new PowerAssert()
}
