## Konfiguracja i uruchomienie
<br/>
1. Activity<br/>
Model Activity znajduje się w pliku ErrorHandling.bpmn20.xml.<br/>
1.1 Zainstaluj i skonfiguruj Tomcata zgodnie z instrukcją laboratoryjną.<br/>
1.2 Wdróż na Tomcata webowe aplikacje Activiti-Explorer i Activiti-Rest zgodnie z instrukcją laboratoryjną (obie muszą komunikować się z tą samą bazą danych)<br/>
<br/>
2. Trello<br/>
2.1 Utwórz nowy Board<br/>
2.2 Board na Trello musi zawierać listy o nazwach: New, Development, Review, Test, Deploy, Done<br/>
2.3 Wygeneruj klucz i token do koumunikacji za pomocą Restowego API <br/>
<br/>
3. SonarCube<br/>
3.1 Zainstaluj aplikację SonarQube zgodnie z instrukcjami podanymi na stronie producenta<br/>
<br/>
4. Jenkins<br/>
4.1 Konfiguracja zadania znajduje się na zrzucie w pliku ServiceTasks\jenkins_job_conf.png<br/>
4.2 Projekt musi wykorzystywać środowisko maven<br/>
<br/>
5. PushApp<br/>
5.1 Skopiuj PushApp/config-sample.json do PushApp/config.json<br/>
5.2 Nie ma potrzeby zmiany konfiguracji (config.json), do pokazowego działania<br/>
5.3 Do budowania pliku .jar z zależnościami użyj skryptu PushApp/build-jar.bat<br/>
5.4 PushApp należy skopiować do folderu, który jest repozytorium .git, i tam go uruchomić<br/>
<br/>
6. TaskCreator<br/>
6.1 Skopiuj TaskCreator/config-sample.json do TaskCreator/config.json<br/>
6.2 W konfiguracji (config.json) należy wypełnić dane dostępowe do API Trello (trelloKey, trelloToken)<br/>
6.3 W konfiguracji (config.json) należy wypełnić id board-a na trello (trelloBoardId), na którym będą zadania<br/>
6.4 Do budowania pliku .jar z zależnościami użyj skryptu TaskCreator/build-and-run.bat<br/>
<br/>
7. ServiceTasks<br/>
7.1 Skopiuj ServiceTasks/config-sample.json do ServiceTasks/config.json<br/>
6.2 W konfiguracji ogólnej (config.json) należy wypełnić dane dostępowe do API Trello (trelloKey, trelloToken)<br/>
6.3 W konfiguracji Jenkins (ServiceTasks/src/main/java/org/miasi/jenkins/JenkinsConfig.java) należy wypełnić:<br/>
~ dane autoryzacyjne (JENKINS_AUTH)<br/>
~ pełną ścieżkę do zadania (JENKINS_TASK_URL)<br/>
~ nazwa pliku .jar (JAR_NAME), który tworzy się podczas budowania paczki<br/>
~ adresy bezwględne folderów, do których następuje deploy, oraz gdzie będzie robiona kopia (BACKUP_FOLDER, DEPLOY_FOLDER)<br/>
6.4 W kodzie analizy Sonar (ServiceTasks/src/main/java/org/miasi/sonar/SonarAnalyzer.java) należy wypełnić:<br/>
~ ścieżkę bezwględną testowanego projektu (filePath, linia 34)<br/>
~ adres do aplikacji sonar (linia 56)<br/>
6.5 Do budowania pliku .jar z zależnościami użyj skryptu ServiceTasks/jar-create.bat<br/>
6.6 Jar należy użyć jako biblioteki w activity-explorer|rest, kopiując plik target\service-tasks-0.1-jar-with-dependencies.jar do:<br/>
~ {Tomcat-path}/webapps/activiti-explorer/WEB-INF/lib directory<br/>
~ {Tomcat-path}/webapps/activiti-rest/WEB-INF/lib directory<br/>
<br/>
7. Serwer e-mail<br/>
7.1 Zainstalować i uruchomić lokalny serwer em-ail (np. Papercut)<br/>
<br/>
8. Uruchomienie<br/>
8.1 Uruchom serwer bazy danych oraz Tomcata<br/>
8.2 Zaloguj się do aplikacji Activiti-Explorer<br/>
8.3 Zaimportuj model procesu i wdróż go<br/>
<br/>
