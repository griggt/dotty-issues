val dottyVersion = "0.27.0-RC1"
val nightlyVersion = "0.28.0-bin-20200924-7ee3a20-NIGHTLY"
lazy val allScalaVersions = List(nightlyVersion, dottyVersion)

lazy val root = project
  .in(file("."))
  .settings(
    name := "i9332",
    version := "0.1.0",
    scalaVersion := dottyVersion,
    crossScalaVersions := allScalaVersions
  )
