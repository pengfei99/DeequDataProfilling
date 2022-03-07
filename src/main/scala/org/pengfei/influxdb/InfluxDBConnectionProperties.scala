package org.pengfei.influxdb

case class InfluxDBConnectionProperties(
                                         serverURLWithPort: String,
                                         dbName: String,
                                         measurementName: String)
