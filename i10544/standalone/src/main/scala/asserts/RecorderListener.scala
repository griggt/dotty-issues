package verify
package asserts

trait RecorderListener[A, R] {
  def valueRecorded(recordedValue: RecordedValue): Unit = {}
  def expressionRecorded(recordedExpr: RecordedExpression[A], recordedMessage: Function0[String]): Unit = {}
  def recordingCompleted(recording: Recording[A], recordedMessage: Function0[String]): R
}
