name := "commons"
version := "0.1.0"
scalaVersion := "3.3.1"


resolvers += "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies ++= dependencies

val AkkaVersion = "2.9.0"
val AkkaHttpVersion = "10.6.0"
lazy val dependencies = Seq(
  "org.scalactic" %% "scalactic" % "3.2.16",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % "test" cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % "test" cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream-kafka" % "5.0.0" cross CrossVersion.for3Use2_13,
  // Logging
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion cross CrossVersion.for3Use2_13,
  "ch.qos.logback" % "logback-classic" % "1.2.13",
)

