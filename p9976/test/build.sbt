scalaVersion := "0.28.0-bin-SNAPSHOT"
name := "p9976-test"
version := "0.1"
libraryDependencies += ("io.grigg" %% "p9976-lib" % "1.0").withDottyCompat(scalaVersion.value)
