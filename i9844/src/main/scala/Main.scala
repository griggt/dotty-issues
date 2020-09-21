object DetSkipOctree {
  sealed trait Leaf  [PL]
  sealed trait Branch[PL]
}
trait DetSkipOctree[PL]

class Impl[PL] extends DetSkipOctree[PL] {
  final type Leaf = DetSkipOctree.Leaf[PL]

  type PLL = PL

  protected trait LeftBranchImpl {
    this: DetSkipOctree.Branch[PL] =>

    def demoteLeaf(point: PLL, leaf: Leaf): Unit = ???
  }
}