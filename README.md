## ServiceTasks ##
In order to use this project in Activiti, you have to follow these steps:<br/>
0. Add configuration (config.json file). Example is provided as config-sample.json<br/>
1. Run jar-create.bat<br/>
2. Jar is now available in path: target\service-tasks-0.1-jar-with-dependencies.jar<br/>
3. Copy the JAR file to {Tomcat-path}/webapps/activiti-explorer/WEB-INF/lib directory<br/> 
4. Copy the JAR file to {Tomcat-path}/webapps/activiti-rest/WEB-INF/lib directory<br/>
4. Restart Tomcat<br/>
<br/>
## ServiceTasks - TrelloStatus ##
TrelloStatus use "trello_card_id" variable. This variable is created automatically by TaskCreator.<br/>
To change status of trello card use one of these classes:<br/>
org.miasi.trello.status.New<br/>
org.miasi.trello.status.Development<br/>
org.miasi.trello.status.Review<br/>
org.miasi.trello.status.Test<br/>
org.miasi.trello.status.Deploy<br/>
org.miasi.trello.status.Done<br/>
<br/>
## TaskCreator ##
0. Add configuration (config.json file). Example is provided as config-sample.json<br/> 
1. Run build-and-run.bat.<br/>
2. Jar is now available in path: target\task-creator-0.1-jar-with-dependencies.jar<br/>
<br/>
Newly created Trello card contains comments with such content:<br/>
task_creator_email -> ?<br/>
activity_process_id -> ?<br/>
<br/>
Newly started Activity process has these info given as process variables:<br/>
task_creator_email<br/>
trello_card_url<br/>
trello_card_id<br/>

