val dottyVersion = "0.27.0-RC1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "i9842",
    version := "0.1.0",

    scalaVersion := dottyVersion
  )