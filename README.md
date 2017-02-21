# Paytm Weblog challenge version 0.001


### Running the application

prerequisites : SBT installed, scala 2.11.8

The application is a sbt application.
1. Browse to the root folder of the project 
2. run sbt 
3. type run to invoke the sbt run task

### Result 

The application will write it output to the results folder in the root of 
project. 
This folder containers the following: 

most_engaged_users_results.csv/*.csv
 - This list the top 40 most engaged users , it list the client:ip 
   and the total time spent in minutes
 
unique_visits_session_results.csv
  - This list the top 40 sessions base on the 
    number of unique urls that are visited during 
    the session
    
average_session_time_result.csv 

- This list average session length in minutes








