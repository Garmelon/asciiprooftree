val scala3Version = "3.5.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "asciiprooftree",
    version := "0.0.0",
    scalaVersion := scala3Version,
    libraryDependencies += "org.rogach" %% "scallop" % "5.1.0",
  )
