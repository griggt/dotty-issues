trait Measure[-C]   // The - is needed

object PathMeasure extends Measure[Long]

trait FingerTree[+A]   // The + is needed
object FingerTree {
  def empty[A](m: Measure[A]): FingerTree[A] = ???
}


trait Test {
  def read(sz: Int): Any = {
    var tree = FingerTree.empty(PathMeasure)  // problem origin
  }
}
