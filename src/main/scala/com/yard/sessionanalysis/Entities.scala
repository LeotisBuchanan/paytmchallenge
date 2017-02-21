package com.yard.sessionanalysis

/**
  * Created by yardman on 2/20/17.
  */
object Entities {

  case class AccessLogRecord(
                              timestamp: java.sql.Timestamp,
                              elb: String,
                              client_port: String,
                              backend_port: String,
                              request_processing_time: Float,
                              backend_processing_time: Float,
                              response_processing_time: String,
                              elb_status_code: String,
                              backend_status_code: String,
                              received_bytes: String,
                              sent_bytes: String,
                              request: String,
                              user_agent: String,
                              ssl_cipher: String
                            )

}
