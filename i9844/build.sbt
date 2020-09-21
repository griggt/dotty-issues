val dottyVersion = "0.27.0-RC1"
// val dottyVersion = "2.13.3"
// val dottyVersion = "2.12.12"
// val dottyVersion = "2.11.12"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-simple",
    version := "0.1.0",

    scalaVersion := dottyVersion
  )
