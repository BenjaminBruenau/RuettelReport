name := "Analysis"
version := "0.1.0-SNAPSHOT"
scalaVersion := "3.3.1"

libraryDependencies ++= dependencies

lazy val dependencies = Seq(
  // Spark -  We don't want to bundle these, as they are provided by the runtime
  "org.apache.spark" %% "spark-core" % "3.5.0" % Provided cross CrossVersion.for3Use2_13,
  "org.apache.spark" %% "spark-sql" % "3.5.0" % Provided cross CrossVersion.for3Use2_13,
)

