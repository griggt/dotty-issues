val scala213 = "2.13.1"
val dotty24 = "0.24.0-RC1"
val dotty25 = "0.25.0-RC2"
val dotty26 = "0.26.0-RC1"
val dotty27 = "0.27.0-RC1"
val dotty3m1 = "3.0.0-M1-bin-20201018-bdf634c-NIGHTLY"

lazy val root = project
  .in(file("."))
  .settings(
    name := "i8847",
    version := "0.1.0",
    scalaVersion := dotty27,
    crossScalaVersions := Seq(scala213, dotty24, dotty25, dotty26, dotty27, dotty3m1),
    libraryDependencies += ("org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2").withDottyCompat(scalaVersion.value)

    // dotty compiled parser-combinators library snapshot, published locally
    //libraryDependencies += ("org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2+133-e26d523e-SNAPSHOT")
  )
