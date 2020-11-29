package verify
package asserts

abstract class Recorder[A] {
  inline def apply(value: A): Unit = ${ RecorderMacro.apply('value) }
}

// one instance per recording
class RecorderRuntime[A] {
  def recordValue[U](value: U): U = ???
  def recordExpression(value: A): Unit = ???
}

class PowerAssert extends Recorder[Boolean]

object PowerAssert {
  def assert: PowerAssert = new PowerAssert()
}
