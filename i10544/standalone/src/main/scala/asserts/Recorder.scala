package verify
package asserts

abstract class Recorder[A, R] {
  protected def listener: RecorderListener[A, R]

  inline def apply(value: A): R =
    ${ RecorderMacro.apply('value, 'listener) }
}

case class Recording[A](recordedExprs: List[RecordedExpression[A]])
case class RecordedValue(value: Any, anchor: Int)
case class RecordedExpression[T](text: String, ast: String, value: T, recordedValues: List[RecordedValue])
