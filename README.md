# ServiceTasks #
In order to use this project in Activiti, you have to follow these steps:<br/>
1. Run jar-create.bat<br/>
2. Jar is now available in path: target\service-tasks-0.1-jar-with-dependencies.jar<br/>
3. Copy the JAR file to {Tomcat-path}/webapps/activiti-explorer/WEB-INF/lib directory<br/> 
4. Copy the JAR file to {Tomcat-path}/webapps/activiti-rest/WEB-INF/lib directory<br/>
4. Restart Tomcat<br/>
<br/>
ServiceTasks must be configured by config.json.<br/>
Sample config.json is provided as config-sample.json.<br/>
config.json must be in folder, from which app is started.<br/>
logs will be available in files: ST_Log_java-class-name.txt<br/>
<br/>
example, in console:<br/>
current path: cd C:\Users\Adrian\Desktop\apache-tomcat-8.0.33<br/>
start tomcat by command: bin\startup.bat<br/>
config.json path: C:\Users\Adrian\Desktop\apache-tomcat-8.0.33\config.json<br/>
logs path: C:\Users\Adrian\Desktop\apache-tomcat-8.0.33\\ST_Log_*.txt<br/>
<br/>
## TrelloStatus ##
TrelloStatus use "trello_card_id" variable. This variable is created automatically by TaskCreator.<br/>
To change status of trello card use one of these classes:<br/>
org.miasi.trello.status.New<br/>
org.miasi.trello.status.Development<br/>
org.miasi.trello.status.Review<br/>
org.miasi.trello.status.Test<br/>
org.miasi.trello.status.Deploy<br/>
org.miasi.trello.status.Done<br/>
<br/>
# MiniSoftwares #
## TaskCreator ##
1. App must be configured - config.json must be provided.<br/>
2. Sample config.json is provided as config-sample.json.<br/>
3. config.json must be in folder, from which app is started.<br/>
3. App can be build&started by build-and-run.bat.<br/>
4. Project properly compiles to jar with dependencies.<br/>
5. jar file will be available in target\task-creator-0.1-jar-with-dependencies.jar<br/>
<br/>
Newly created Trello card contains comments with such content:<br/>
task_creator_email -> ?<br/>
activity_process_id -> ?<br/>
<br/>
Newly started Activity process has these info given as process variables:<br/>
task_creator_email<br/>
trello_card_url<br/>
trello_card_id<br/>

