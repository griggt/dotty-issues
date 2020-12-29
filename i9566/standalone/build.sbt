scalaVersion := "3.0.0-M3"
crossScalaVersions := List("3.0.0-M3", "0.26.0-RC1", "2.13.4")

libraryDependencies ++= {
  if (isDotty.value)
    Nil
  else
    Seq(
      compilerPlugin("org.typelevel" % "kind-projector" % "0.11.2" cross CrossVersion.full),
    )
}

scalacOptions ++= {
  if (isDotty.value)
    Seq("-Ykind-projector")
  else
    Nil
}
