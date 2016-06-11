copy config.json src\main\resources\config.json && ^
mvn clean package && ^
java -jar target\push-app-0.1-jar-with-dependencies.jar
pause
