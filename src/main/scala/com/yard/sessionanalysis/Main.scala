package com.yard.sessionanalysis


import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._


/**
  * Created by yardman on 2/20/17.
  */
object Main {


  def main(args: Array[String]): Unit = {

    val sparkSession = SparkSession.builder()
      .appName("PayTm Challenge")
      .master("local[*]")
      .getOrCreate()


    sparkSession.udf.register("computeElapseTimeMinutes", Utils.computeElapseTimeMinutes)

    // get the setting values from the config file
    val datapath: String = Settings.getDataPath("sessionanalysis.datapath")
    val session_interval = Settings.getSettingSessionInterval("sessionanalysis.session_interval")

    val most_engage_users_path: String = Settings
      .getSetting("sessionanalysis.most_engage_users_result_path")
    val average_session_length: String = Settings
      .getSetting("sessionanalysis.average_session_length_result_path")
    val unique_url_visits_session_path: String = Settings
      .getSetting("sessionanalysis.unique_visits_session_result_path")


    //load the file
    val logs_rdd = sparkSession.sparkContext.textFile(datapath)

    //implicits for converting rdd to dataframes
    import sparkSession.implicits._
    val accessLogsDF = logs_rdd.map(row => Utils.parseAccessRecord(row)).toDF()


    val clientTimestampDF = accessLogsDF.select("client_port", "timestamp", "request")


    //clientTimestampDFSorted.show(20,false)
    val lagsDF = clientTimestampDF.
      select('client_port, 'timestamp, 'request,
        lag('timestamp, 1).over(
          Window.partitionBy('client_port).orderBy('client_port, 'timestamp)) as "lag")


    val lagsDF_without_nulls = lagsDF.withColumn("lag",
      when($"lag".isNull, $"timestamp")
        .otherwise($"lag"))


    lagsDF_without_nulls.createOrReplaceTempView("lagsDFTable")


    val sqlDF = sparkSession.sql(
      """SELECT client_port,
         request , timestamp, lag,
         computeElapseTimeMinutes(timestamp,lag) as session_duration FROM lagsDFTable""")


    val statesDF = sqlDF.withColumn("state",
      when($"session_duration" > session_interval, 1)
        .otherwise(0))

    statesDF.createOrReplaceTempView("sessionsTable")

    val session_idDF = sparkSession.sql(
      """
     SELECT
      client_port,
      request,
      state,
      timestamp,
      lag,
      session_duration,
      sum(state) OVER (ORDER BY client_port) as session_id
     FROM
     sessionsTable
     """
    )


    /**
      * Average Session Time
      *  - compute the avereage duration of the
      * sessions
      *
      */


    session_idDF.select(avg("session_duration").alias("Avg_Session_Time_mins")).show()

    /**
      * Most Engage Users
      * Find the most engage users
      */


    val mostEngagedUsersDF = session_idDF.groupBy("client_port").
      agg(sum("session_duration").alias("total_time_onsite")).
      orderBy(desc("total_time_onsite"))

    //write the top 40 engaged users to the results file

    mostEngagedUsersDF
      .limit(40)
      .repartition(1)
      .write.csv(most_engage_users_path)




    //Determine unique URL visits per session.

    val uniqueURLVisitsPerSessionDF = session_idDF.select($"session_id", $"request")
      .distinct.groupBy("session_id").count().alias("num_unique_urls")

    //write the top 40 sessions to file
    uniqueURLVisitsPerSessionDF
      .limit(40)
      .repartition(1)
      .write.csv(unique_url_visits_session_path)



  }

}
