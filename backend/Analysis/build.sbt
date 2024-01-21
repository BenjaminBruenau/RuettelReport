name := "Analysis"
version := "0.1.0-SNAPSHOT"
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
  "org.apache.spark" % "spark-sql-kafka-0-10" % sparkVersion % Provided cross CrossVersion.for3Use2_13,
  "org.apache.spark" %% "spark-mllib" % sparkVersion % Provided cross CrossVersion.for3Use2_13, // ToDo: MLLIB using SPIRE?
  "com.intel.analytics.bigdl" % "bigdl-dllib-spark_3.2.3" % "2.3.0-SNAPSHOT" from "https://oss.sonatype.org/content/repositories/snapshots/com/intel/analytics/bigdl/bigdl-dllib-spark_3.2.3/2.3.0-SNAPSHOT/bigdl-dllib-spark_3.2.3-2.3.0-20230207.092630-1.jar",
  //"org.mongodb.spark" %% "mongo-spark-connector" % "10.2.1" cross CrossVersion.for3Use2_13, - incompatible with Spark 3.5.0
  "com.github.kkurt" % "mongo-spark" % "main-SNAPSHOT" cross CrossVersion.for3Use2_13,
  "io.github.vincenzobaz" %% "spark-scala3-encoders" % "0.2.6",
  "io.github.vincenzobaz" %% "spark-scala3-udf" % "0.2.6"
)

