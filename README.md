# Deequ Data Profiling

For small data, we can use pandas data profiling to understand data, but for big data, pandas will fail.

We can use Deequ(spark) to do the calculation. And store the result metric inside a timeserie database (e.g. influxdb)

Then use the grafana to view the metric

## Run InfluxDB :

```shell
# pull influxDB docker image
docker pull influxdb:1.8.4
# run docker container
docker run --rm --name=influxdb -d -p 8086:8086 influxdb:1.8.4
# connect to docker container 
docker exec -it influxdb /bin/bash
# run influxDB console
influx
# create database which we will use for storing our metrics
create database metric
```

## run Grafana (use new console tab):

```shell
# pull grafana docker image
docker pull grafana/grafana
# run Grafana docker container & connect it with influxDB container
docker run -d --rm --name=grafana -p 3000:3000 --link influxdb grafana/grafana
```

## Generate metric

You can generate metric by calling org.pengfei.dataprofiler.GenerateMetric.

You need to check/modify the below config value

```scala
val filePath = "/home/pliu/Downloads/2009_01_data.parquet"
val influxDdUrl = "http://localhost:8086"
val influxDbName = "metric"
val influxTableName = "DataMetrics"
```

## Check the generated metrics

You need to get in your influxdb docker container first

```shell
# connect to docker container 
docker exec -it influxdb /bin/bash
# run influxDB console
influx

# connect to database
use metric

# show columns of table 
select * from DataMetrics
```

You should see below lines

```text
2022-03-07T13:50:41.504Z 1646661033765 Dataset *               Size         /home/pliu/Downloads/2009_01_data.parquet orders           14092413
2022-03-07T13:50:42.593Z 1646661033765 Column  passenger_count Completeness /home/pliu/Downloads/2009_01_data.parquet orders           1
2022-03-07T13:50:42.708Z 1646661033765 Column  vendor_id       Completeness /home/pliu/Downloads/2009_01_data.parquet orders           1
2022-03-07T13:51:10.796Z 1646661067890 Dataset *               Size         /home/pliu/Downloads/2009_01_data.parquet orders           14092413
2022-03-07T13:51:10.95Z  1646661067890 Column  passenger_count Completeness /home/pliu/Downloads/2009_01_data.parquet orders           1
2022-03-07T13:51:11.019Z 1646661067890 Column  vendor_id       Completeness /home/pliu/Downloads/2009_01_data.parquet orders           1
2022-03-07T14:05:52.948Z 1646661949776 Dataset *               Size         /home/pliu/Downloads/2009_01_data.parquet orders           14092413
2022-03-07T14:05:53.077Z 1646661949776 Column  passenger_count Completeness /home/pliu/Downloads/2009_01_data.parquet orders           1
2022-03-07T14:05:53.115Z 1646661949776 Column  vendor_id       Completeness /home/pliu/Downloads/2009_01_data.parquet orders           1
2022-03-07T14:06:14.16Z  1646661971207 Dataset *               Size         /home/pliu/Downloads/2009_01_data.parquet orders           14092413

```

## Use grafana to connect to view the generated metrics

### Create data source
Open the grafana web UI (http://localhost:3000/). The default login is admin:admin

You need to create a datasource to connect to influxdb. You need to enter the following info in the datasource creation tab

```text
Name: InfluxDB
URL: http://influxdb:8086
Database: metric
User: admin
Password: admin
```

### Create dashboard

Then create a dashboard with various panel that visualize your metric.

To create a dashboard, you need to edit the query.
For example, 
- if you want to view the count of rows, you can select value column and set filter **entity='Dataset'**
- if you want to view the completeness of column vendor_id, you can select value and set filter **entity='Column' AND instance='vendor_id''**

