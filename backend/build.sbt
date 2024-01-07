

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "Ruettel Report Backend",
    libraryDependencies ++= dependencies,
  )

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

val AkkaVersion = "2.9.0"
val AkkaHttpVersion = "10.6.0"
lazy val dependencies = Seq(
  "org.scalactic" %% "scalactic" % "3.2.16",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "org.scala-lang.modules" % "scala-parser-combinators_3" % "2.3.0",
  "org.scala-lang.modules" % "scala-xml_3" % "2.2.0",
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % "test",
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "5.0.0",

  // Logging
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.13",
)



lazy val analysis = (project in file("Analysis"))
