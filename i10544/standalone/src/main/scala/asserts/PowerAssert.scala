package verify
package asserts

/**
 * An instance of PowerAssert returns an object that can be called
 * with a signature of `assert(...)` function.
 */
class PowerAssert extends Recorder[Boolean, Unit] {
  override lazy val listener: RecorderListener[Boolean, Unit] = ??? //new AssertListener
}
