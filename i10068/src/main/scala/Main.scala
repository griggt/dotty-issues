import scala.math.Ordering
import zio.prelude.{Ord, PartialOrd}
import zio.test.Gen
import zio.test.laws._

/*
object OptOrdering extends Ordering[Option[Int]] {
  def compare(a: Option[Int], b: Option[Int]): Int = 0
}

object Main {

  def main(args: Array[String]): Unit = {
    //val o: Ord[Int] = Ord.default

    val x: Ordering[Option[Int]] = OptOrdering
    val y = Ord.default(x)
  }
}
 */


// Why does this work fine???
/*
object Main {
  def main(args: Array[String]): Unit = {
    val o = implicitly[PartialOrd[Option[Int]]]
    val r = o.compare(Some(0), Some(1))
    println(o)
    println(r)

    val oo = implicitly[Ord[Option[Int]]]
    val rr = oo.compare(Some(0), Some(1))
    println(oo)
    println(rr)
  }
}
*/

/*
object Main {
  val genSomeOne: Gen[Any, Option[Int]] = Gen.const(Some(1))

  def main(args: Array[String]): Unit = {
    val o = implicitly[PartialOrd[Option[Int]]]
    val r = o.compare(Some(0), Some(1))
    println(o)
    println(r)

    val oo = implicitly[Ord[Option[Int]]]
    val rr = oo.compare(Some(0), Some(1))
    println(oo)
    println(rr)
  }
}
 */


// Woohoo ! This one blows up!
object Main {
  def main(args: Array[String]): Unit = {
    //val gen: Gen[Any, Option[Int]] = Gen.const(Some(1))
    val law = Ord.dummyLaw2 // Ord.connexityLaw2
    law.apply(Some(1), Some(2))
  }
}

