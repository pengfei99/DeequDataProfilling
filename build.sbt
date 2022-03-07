ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.12"

ThisBuild / libraryDependencies += "com.amazon.deequ" % "deequ" % "2.0.1-spark-3.2"
ThisBuild / libraryDependencies += "org.influxdb" % "influxdb-java" % "2.21"
ThisBuild / libraryDependencies += "org.apache.spark" %% "spark-core" % "3.2.1" % "provided"
ThisBuild / libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.2.1" % "provided"

lazy val root = (project in file("."))
  .settings(
    name := "SparkDeequDataProfiling"
  )
