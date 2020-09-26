val dottyVersion = "0.23.0-bin-20200301-d989caf-NIGHTLY"

lazy val root = project
  .in(file("."))
  .settings(
    name := "i8424",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
  )
