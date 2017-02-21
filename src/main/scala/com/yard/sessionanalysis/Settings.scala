package com.yard.sessionanalysis

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by yardman on 2/21/17.
  */
object Settings {

  //load the default config
  val conf:Config = ConfigFactory.load()

  /**
    * Function reads the config file and
    * returns the value associated with
    * give key, in this case the sessionInterval
    * @param key : a key in the config file
    * @return : the value that the key maps to
    */
  def getSettingSessionInterval(key:String):Int = {

    val sessionInterval = conf.getInt(key)
    sessionInterval

  }


  /**
    * returns settings that have
    * a string type
    * @param key; The key for the setting
    * @return: The value of setting associated
    *          with the given key
    */
  def getSetting(key:String):String = {

    val setting = conf.getString(key)
    setting

  }





  /**
    * Function reads the config file and
    * returns the value associated with
    * give key, in this case the path to the
    * data file
    * @param key : a key in the config file
    * @return : the value that the key maps to
    */
  def getDataPath(key:String):String = {

    val dataPath = conf.getString(key)
    dataPath

  }

}


