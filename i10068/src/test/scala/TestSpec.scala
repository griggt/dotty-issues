import zio.test._
import zio.test.laws._

import zio.prelude.Ord

// This blows up as expected
object TestSpec extends DefaultRunnableSpec {
  def spec: ZSpec[Environment, Failure] =
    suite("OrdSpec")(
      testM("option")(checkAllLaws(Ord)(Gen.option(Gen.anyInt))), // BAD
      //testM("boolean")(checkAllLaws(Ord)(Gen.boolean)),   // GOOD
    )
}
