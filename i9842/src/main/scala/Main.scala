trait Measure[-C]      // The - is needed
trait FingerTree[+A] { // The + is needed
  def op[A1 >: A](a: A1): A
}

trait X {
  def empty[A](m: Measure[A]): FingerTree[A]

  def test(meas: Measure[Int]): Int = {
    val tree = empty(meas)
    return tree.op(2) + 1
  }

}
