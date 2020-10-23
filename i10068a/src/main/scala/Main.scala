import zio.prelude.Ord

// Woohoo ! This one blows up!
object Main {
  def main(args: Array[String]): Unit = {
    val law = Ord.dummyLaw2
    val x = Some(1)
    val y = Some(2)
    law.apply(x, x)  // ok
    law.apply(x, y)  // blows up
    //law.apply(Some(1), Some(1))    // blows up
  }
}

