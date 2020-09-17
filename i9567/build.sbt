//val dottyVersion = "0.26.0-RC1"
val dottyVersion = "0.27.0-RC1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "i9567",
    version := "0.1.0",

    scalaVersion := dottyVersion,
    //scalacOptions ++= Seq("-Ykind-projector"),

    //libraryDependencies += "org.typelevel" %% "cats-effect" % "2.2-e395f06-SNAPSHOT"
  )
