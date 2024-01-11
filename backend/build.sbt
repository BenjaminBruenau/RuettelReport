

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "Ruettel Report Backend",
    libraryDependencies ++= dependencies,
    excludeDependencies += "org.scala-lang.modules" % "scala-xml_3" // fixes conflicting cross-version suffixes for scala-xml _3 & _2.13
  )

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

val AkkaVersion = "2.9.0"
val AkkaHttpVersion = "10.6.0"
lazy val dependencies = Seq(
  "org.scalactic" %% "scalactic" % "3.2.16",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0",
  "org.scala-lang.modules" %% "scala-xml" % "2.2.0" cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % "test" cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % "test" cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http-xml" % AkkaHttpVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream-kafka" % "5.0.0" cross CrossVersion.for3Use2_13,
  ("com.lightbend.akka" %% "akka-stream-alpakka-json-streaming" % "7.0.1" cross CrossVersion.for3Use2_13),
    //.exclude("com.typesafe.akka", "akka-stream_2.13"), //->java.lang.NoSuchMethodError: 'void akka.stream.stage.InHandler.$init$(akka.stream.stage.InHandler)

  // Logging
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion cross CrossVersion.for3Use2_13,
  "ch.qos.logback" % "logback-classic" % "1.2.13",
)



lazy val analysis = (project in file("Analysis"))
