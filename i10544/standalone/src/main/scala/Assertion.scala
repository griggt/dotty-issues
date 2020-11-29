package verify

import verify.asserts.PowerAssert

trait Assertion /*extends asserts.AssertEquals[Unit]*/ {
  lazy val assert: PowerAssert = new PowerAssert()
}
