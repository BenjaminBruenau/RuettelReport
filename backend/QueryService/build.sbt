name := "QueryService"
version := "0.1.0"
scalaVersion := "3.3.1"

fork := true // When running locally HttpServer will otherwise shutdown immediately
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
  "com.lightbend.akka" %% "akka-stream-alpakka-json-streaming" % "7.0.1" cross CrossVersion.for3Use2_13,
  // Logging
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion cross CrossVersion.for3Use2_13,
  "ch.qos.logback" % "logback-classic" % "1.2.13",

  // > cd backend/commons  > sbt publishLocal
  "default" %% "commons" % "0.1.0" excludeAll ExclusionRule("*") // We already have all of those dependencies here
)

// sbt native-packager Docker Settings
import com.typesafe.sbt.packager.docker._

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerBaseImage := "pnavato/amazoncorretto-jre:17-alpine"
dockerExposedPorts ++= Seq(8080)
dockerEntrypoint := Seq.empty

dockerCommands ++= Seq(
  Cmd("ENV", "KAFKA_BOOTSTRAP_SERVERS", "localhost:29092"), // This will be overridden when running!
  ExecCmd("ENTRYPOINT", "sh", "-c", "bin/" + s"${executableScriptName.value}" + " -Dport=$KAFKA_BOOTSTRAP_SERVERS")
)
