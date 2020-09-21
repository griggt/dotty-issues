trait Measure[-C]      // The - is needed
trait FingerTree[+A]   // The + is needed

object X {
  def empty[A](m: Measure[A]): FingerTree[A] = ???

  def test(meas: Measure[Long]): FingerTree[Long] = {
    val tree = empty(meas)  // problem origin
    return tree
  }

}
