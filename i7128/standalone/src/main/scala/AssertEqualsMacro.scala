import scala.language.experimental.macros

trait AssertEquals[R] {
  protected def stringAssertEqualsListener: RecorderListener[String, R]
  inline def assertEquals(expected: String, found: String): R =
    ${ StringRecorderMacro.apply('expected, 'found, 'stringAssertEqualsListener) }

  inline def assertEquals(expected: String, found: String, message: => String): R =
    ${ StringRecorderMacro.apply('expected, 'found, 'message, 'stringAssertEqualsListener) }
  }
