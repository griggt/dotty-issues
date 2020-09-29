val dotty21 = "0.21.0-RC1"
val dotty26 = "0.26.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "i7128",
    version := "0.1.0",
    scalaVersion := dotty26,
    scalacOptions ++= Seq("-color:never"),
    crossScalaVersions := List(dotty21, dotty26),
)
