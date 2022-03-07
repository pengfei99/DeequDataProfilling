package org.pengfei.dataprofiler

import com.amazon.deequ.analyzers.runners.AnalysisRunner
import com.amazon.deequ.analyzers.runners.AnalyzerContext.successMetricsAsDataFrame
import com.amazon.deequ.analyzers._
import com.amazon.deequ.repository.ResultKey
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.pengfei.influxdb.{InfluxDBConnectionProperties, InfluxDBMetricsRepository}

object GenerateMetric extends App {
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)
  // setup config val
  val filePath = "/home/pliu/Downloads/2009_01_data.parquet"
  val influxDdUrl= "http://localhost:8086"
  val influxDbName="metric"
  val influxTableName="DataMetrics"
  // build spark session
  val spark = initSpark()
  val df=spark.read.parquet(filePath)
  println(s"total row: ${df.count()}")

  val influxDBConnectionProperties = InfluxDBConnectionProperties(influxDdUrl, influxDbName, influxTableName )

  val resultKey = ResultKey(
    System.currentTimeMillis(),
    Map("dataSetFilePath" -> filePath, "dataSetName" -> "orders"))

  // set up analyzer rules
  val analysisResult = AnalysisRunner
    .onData(df)
    .useRepository(new InfluxDBMetricsRepository(influxDBConnectionProperties))
    .saveOrAppendResult(resultKey)
    .addAnalyzer(Size())
    .addAnalyzer(Completeness("passenger_count"))
    .addAnalyzer(Completeness("vendor_id"))
    .run()

  val metric = successMetricsAsDataFrame(spark, analysisResult)

  metric.show(false)

  spark.close()

  def initSpark(isLocalRun: Boolean = true): SparkSession = {
    val sparkSessionBuilder =
      SparkSession
        .builder
        .appName(this.getClass.getSimpleName)

    val spark =
      if (isLocalRun) {
        sparkSessionBuilder
          .master("local[*]")
          .getOrCreate()
      } else
        sparkSessionBuilder.getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")
    spark.conf.set("spark.sql.session.timeZone", "UTC")

    spark
  }

}
