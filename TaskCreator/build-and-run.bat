copy config.json src\main\resources\config.json && ^
mvn clean package && ^
java -jar target\task-creator-0.1-jar-with-dependencies.jar
pause
