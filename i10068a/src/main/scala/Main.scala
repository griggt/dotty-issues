import zio.prelude.{ Ord, PartialOrd }

// Woohoo ! This one blows up!
object Main {
  def viaDummyLaw(): Unit = {
    val law = Ord.dummyLaw2
    val x = Some(1)
    val y = Some(2)
    law.apply(x, x)  // ok
    law.apply(x, y)  // blows up
    //law.apply(Some(1), Some(1))    // blows up
  }

  ///////////////////////////////////////////////

  def doIt[A: Ord](a: A, b: A): Boolean = {
    println(a)
    val o = implicitly[Ord[A]]
    println(o)
    val r = o.compare(a, b)
    println(r)   // This is ok!

    val oo = implicitly[PartialOrd[A]]
    val rr = oo.compare(a, b)   // But this blows up. It is similar to what lessOrEqual is doing
    println(s"rr = $rr")

    true
  }

  def directly(): Unit = {
    val x = Some(1)
    val y = Some(2)
    doIt(x, x)   // ok  -- edit: not anymore. something changed by checkpoint 6f (1.0.0-RC1+45-b98cc083-SNAPSHOT)
    //doIt(x, y)   // kablammo
  }

  def main(args: Array[String]): Unit = {
    //viaDummyLaw()
    directly()
  }
}

