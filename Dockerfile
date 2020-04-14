FROM openjdk:14-jdk-alpine
ARG JAR_FILE

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app.jar"]
