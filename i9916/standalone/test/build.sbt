scalaVersion := "0.28.0-bin-20200928-09eaed7-NIGHTLY"
name := "i9916-test"
version := "0.1"
libraryDependencies += ("io.grigg" %% "i9916-lib" % "1.0").withDottyCompat(scalaVersion.value)
