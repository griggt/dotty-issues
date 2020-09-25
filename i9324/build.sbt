val dottyVersion = "0.27.0-RC1"
val stableVersion = "0.26.0"
val dotty25rc = "0.25.0-RC1"
val nightlyVersion = "0.28.0-bin-20200924-7ee3a20-NIGHTLY"
lazy val allScalaVersions = List(nightlyVersion, dottyVersion, stableVersion, dotty25rc)

lazy val root = project
  .in(file("."))
  .settings(
    name := "i9324",
    version := "0.1.0",
    scalaVersion := dottyVersion,
    crossScalaVersions := allScalaVersions
  )
