val dotty21 = "0.21.0-RC1"
val dotty22 = "0.22.0"
val dotty23 = "0.23.0"
val dotty24 = "0.24.0"
val dotty25 = "0.25.0"
val dotty26 = "0.26.0"
val dotty27 = "0.27.0-RC1"
val nightly = "0.28.0-bin-20200928-09eaed7-NIGHTLY"

lazy val root = project
  .in(file("."))
  .settings(
    name := "i7128",
    version := "0.1.0",
    scalaVersion := dotty26,
    scalacOptions ++= Seq("-color:never"),
    crossScalaVersions := List(dotty21, dotty22, dotty23, dotty24, dotty25, dotty26, dotty27, nightly),
    unmanagedSourceDirectories in Compile += {
      val sourceDir = (sourceDirectory in Compile).value
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((0, n)) if n < 23 => sourceDir / "scala-0.21"
        case _                      => sourceDir / "scala-0.26"
      }
    }
)
