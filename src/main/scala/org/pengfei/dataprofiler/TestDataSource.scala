package org.pengfei.dataprofiler

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object TestDataSource {

  def main(args: Array[String]):Unit={
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val spark = SparkSession.builder().master("local[2]").appName("Lesson04_8_Spark_SQL_UDF").getOrCreate()

    val filePath = "/home/pliu/Downloads/2009_01_data.parquet"
    val df=spark.read.parquet(filePath)
    df.show()
  }
}
