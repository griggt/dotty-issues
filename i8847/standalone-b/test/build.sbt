scalaVersion := "0.24.0-RC1"
name := "i8847-test"
version := "0.1"
libraryDependencies += ("io.grigg" %% "i8847b-lib" % "1.0-SNAPSHOT").withDottyCompat(scalaVersion.value)
