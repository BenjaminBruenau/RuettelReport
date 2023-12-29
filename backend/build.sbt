import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "Ruettel Report Backend",
    libraryDependencies ++= dependencies,
  )

val AkkaVersion = "2.8.5"
val AkkaHttpVersion = "10.5.3"
lazy val dependencies = Seq(
  "org.scalactic" %% "scalactic" % "3.2.16",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "org.scala-lang.modules" % "scala-parser-combinators_3" % "2.3.0",
  "org.scala-lang.modules" % "scala-xml_3" % "2.2.0",
  "com.lihaoyi" %% "fastparse" % "3.0.2",
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % "test",
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml" % AkkaHttpVersion
)