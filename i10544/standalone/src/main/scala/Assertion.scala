package verify

import verify.asserts.PowerAssert

trait Assertion {
  lazy val assert: PowerAssert = new PowerAssert()
}
