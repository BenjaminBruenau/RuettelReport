name := "Analysis"
version := "0.1.0"
scalaVersion := "3.3.1"

libraryDependencies ++= dependencies


val sparkVersion = "3.5.0"
resolvers += "jitpack" at "https://jitpack.io"
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
//resolvers ++= Resolver.sonatypeOssRepos("snapshots")
//resolvers += "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"

lazy val dependencies = Seq(
  // Spark -  We don't want to bundle these, as they are provided by the runtime
  "org.apache.spark" %% "spark-core" % sparkVersion % Provided cross CrossVersion.for3Use2_13,
  "org.apache.spark" %% "spark-sql" % sparkVersion % Provided cross CrossVersion.for3Use2_13,
  "org.apache.spark" %% "spark-mllib" % sparkVersion % Provided cross CrossVersion.for3Use2_13,
  "org.apache.spark" % "spark-sql-kafka-0-10" % sparkVersion % Provided cross CrossVersion.for3Use2_13, // We want to add specific dependencies dynamically to spark jobs (as they differ between jobs) and not include them in the uber jar
  //"org.mongodb.spark" %% "mongo-spark-connector" % "10.2.1" cross CrossVersion.for3Use2_13, - incompatible with Spark 3.5.0
  "com.github.kkurt" % "mongo-spark" % "main-SNAPSHOT" % Provided cross CrossVersion.for3Use2_13,
  "io.github.vincenzobaz" %% "spark-scala3-encoders" % "0.2.6",
  "io.github.vincenzobaz" %% "spark-scala3-udf" % "0.2.6",

  // > cd backend/commons  > sbt publishLocal
  "default" %% "commons" % "0.1.0" excludeAll ExclusionRule("*") // We already have all of those dependencies here
)

// OutOfMemory-Error: set SBT_OPTS="-Xmx4G"
ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

/*
import com.typesafe.sbt.packager.docker._
dockerCommands := Seq()

dockerCommands := Seq(
  Cmd("FROM", "FROM bennib99/spark:3.5.0-17-alpine"),
  Cmd("WORKDIR", "/app"),
  Cmd("COPY", "/target/scala-3.3.1/Analysis-assembly-0.1.0.jar /opt/spark/jars/analysis.jar"),
  ExecCmd("CMD", "java", "-cp", "/opt/spark/jars/analysis.jar", "run")
)

 */