import scala.language.experimental.macros

abstract class Recorder[A, R] {
  def listener: RecorderListener[A, R] = ???

  inline def apply(value: A): R = ${ RecorderMacro.apply('value, 'listener) }
  //inline def apply(value: A, message: => String): R = ${ RecorderMacro.apply('value, 'message, 'listener) }
}
