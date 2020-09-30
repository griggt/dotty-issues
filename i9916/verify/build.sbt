val nightly = "0.28.0-bin-20200928-09eaed7-NIGHTLY"
val dotty27 = "0.27.0-RC1"  // blows up
val dotty26 = "0.26.0"      // blows up
val dotty25 = "0.25.0"      // ok
val dotty24 = "0.24.0"      // ok

lazy val root = project
  .in(file("."))
  .settings(
    name := "i9916",
    version := "0.1.0",
    scalaVersion := dotty27,
    crossScalaVersions ++= Seq(nightly, dotty27, dotty26, dotty25, dotty24),
    libraryDependencies += ("com.twitter" %% "finagle-core" % "20.9.0").withDottyCompat(scalaVersion.value)
  )
