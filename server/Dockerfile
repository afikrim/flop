FROM openjdk:11

COPY . /usr/src/app

WORKDIR /usr/src/app

ENTRYPOINT ["./mvnw","-Dspring.config.location=classpath:/application.properties","spring-boot:run"]