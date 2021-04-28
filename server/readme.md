# Flop

How to start?

Make sure that you have docker and docker-compose installed,

Then run with this command, your application will start on port 8080

```sh
docker-compose up --build
```

If you want to run the test, you can try this

```sh
SPRING_DATASOURCE_URL="jdbc:mysql://127.0.0.1:3306/flop?autoReconnect=true&failOverReadOnly=false&maxReconnects=10" SPRING_DATASOURCE_USERNAME=flop SPRING_DATASOURCE_PASSWORD=flipFlop ./mvnw -f pom.xml test
```
