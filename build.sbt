lazy val root = project
  .in(file("."))
  .settings(
    organization := "de.plugh",
    name := "asciiprooftree",
    version := "0.0.0",
    scalaVersion := "3.5.0",
    libraryDependencies += "org.rogach" %% "scallop" % "5.1.0",
    assembly / assemblyJarName := s"${name.value}.jar",
  )
