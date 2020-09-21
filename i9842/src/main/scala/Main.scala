trait Measure[-C, M]   // The - is needed

object PathMeasure extends Measure[Long, Int]

trait FingerTree[V, +A]   // The + is needed
object FingerTree {
  def empty[V, A](m: Measure[A, V]): FingerTree[V, A] = ???
}


trait Test {
  def read(sz: Int): Any = {
    var tree = FingerTree.empty(PathMeasure)  // problem origin
    //tree = tree :+ readPathComponent()
  }
}
