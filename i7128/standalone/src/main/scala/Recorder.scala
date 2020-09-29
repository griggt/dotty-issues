import scala.language.experimental.macros

abstract class Recorder[A, R] {
  inline def apply(value: A): R = ${ RecorderMacro.apply('value) }
}
