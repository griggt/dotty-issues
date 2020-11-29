package verify
package asserts

abstract class Recorder[A, R] {
  inline def apply(value: A): R =
    ${ RecorderMacro.apply('value) }
}

case class Recording[A](recordedExprs: List[RecordedExpression[A]])
case class RecordedValue(value: Any, anchor: Int)
case class RecordedExpression[T](text: String, ast: String, value: T, recordedValues: List[RecordedValue])

trait RecorderListener[A, R] {
  def valueRecorded(recordedValue: RecordedValue): Unit = {}
  def expressionRecorded(recordedExpr: RecordedExpression[A], recordedMessage: Function0[String]): Unit = {}
  def recordingCompleted(recording: Recording[A], recordedMessage: Function0[String]): R
}
