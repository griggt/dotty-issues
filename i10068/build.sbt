val dottyVersion = "0.27.0-RC1"
//val zioversion = "1.0.0-RC1+23-6a9b7831+20201022-1437-SNAPSHOT"
//val zioversion = "1.0.0-RC1+23-6a9b7831+20201022-1510-SNAPSHOT"

val zioversion = "1.0.0-RC1+23-6a9b7831+20201022-1706-SNAPSHOT"
//val zioversion = "1.0.0-RC1+24-ff300925+20201022-1835-SNAPSHOT"

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-simple",
    version := "0.1.0",
    scalaVersion := dottyVersion,
    libraryDependencies += "dev.zio" %% "zio-prelude" % zioversion,
    libraryDependencies += "dev.zio" %% "zio-test" % "1.0.3",
    libraryDependencies += "dev.zio" %% "zio-test-sbt" % "1.0.3",
  )
