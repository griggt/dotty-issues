val dotty22 = "0.22.0-RC1"
val dotty23 = "0.23.0-RC1"
val dotty24 = "0.24.0-RC1"
val dotty25 = "0.25.0-RC1"
val dotty26 = "0.26.0-RC1"
val dotty27 = "0.27.0-RC1"
val nightly = "0.28.0-bin-20200927-d084b8b-NIGHTLY"

lazy val root = project
  .in(file("."))
  .settings(
    name := "i8521",
    version := "0.1.0",
    scalaVersion := dotty23,
    crossScalaVersions := Seq(dotty22, dotty23, dotty24, dotty25, dotty26, dotty27, nightly)
  )
