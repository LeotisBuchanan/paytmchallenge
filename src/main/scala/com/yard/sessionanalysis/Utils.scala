package com.yard.sessionanalysis

import java.sql.Timestamp
import java.io.File
import java.util.Random
import com.yard.sessionanalysis.Entities.AccessLogRecord

/**
  * Created by yardman on 2/20/17.
  */
object Utils {

  def safe[S, T](f: S => T): S => Either[T, (S, Exception)] = {
    new Function[S, Either[T, (S, Exception)]] with Serializable {
      def apply(s: S): Either[T, (S, Exception)] = {
        try {
          Left(f(s))
        } catch {
          case e: Exception => Right((s, e))
        }
      }
    }
  }





  val computeElapseTimeMinutes = (ts2: java.sql.Timestamp, ts1:java.sql.Timestamp) => {

    val diff_minutes = (ts2.getTime - ts1.getTime)/(1000 * 60)
    diff_minutes

  }


  def parseDate(dateString:String):java.sql.Timestamp = {
    val dateString_ = dateString.replace("T"," ").replace("Z", "")
    val ts = Timestamp.valueOf(dateString_)
    ts

  }


  def parseAccessRecord(line:String):AccessLogRecord = {

    //TODO replace this with regex extractor

    //just few constants to make things a bit more readable
    val timestamp_idx = 0
    val elb_idx = 1
    val client_port_idx = 2
    val backend_port_idx = 3
    val request_processing_time_idx = 4
    val backend_processing_time_idx = 5
    val response_processing_time_idx = 6
    val elb_status_code_idx = 7
    val backend_status_code_idx = 8
    val received_bytes_idx = 9
    val sent_bytes_idx = 10
    val request_idx = 11
    val user_agent_idx = 12
    val ssl_cipher_idx = 13
    val ssl_protocol_idx = 14

    val fields = line.split("\"")

    val part0 = fields(0).split("\\s+")
    val timestamp = parseDate(part0(timestamp_idx).trim)
    val elb = part0(elb_idx)
    val client_port = part0(client_port_idx)
    val backend_port = part0(3)
    val request_processing_time = part0(4).toFloat
    val backend_processing_time = part0(5).toFloat
    val response_processing_time = part0(6)
    val elb_status_code =  part0(7)
    val backend_status_code =  part0(8)
    val received_bytes  = part0(9)
    val sent_bytes = part0(10)
    val request = fields(1)
    val space   = fields(2)
    val user_agent   = fields(3).trim
    val ssl_cipher = fields(4).trim

    val rand = new Random(System.currentTimeMillis())
    val x = rand.nextInt(1000).toString

    AccessLogRecord(parseDate(part0(timestamp_idx).trim),
      elb,
      part0(client_port_idx),
      backend_port,
      request_processing_time,
      backend_processing_time,
      response_processing_time,
      elb_status_code,
      backend_status_code,
      received_bytes,
      sent_bytes,
      request,
      user_agent,
      ssl_cipher

    )



  }



}
