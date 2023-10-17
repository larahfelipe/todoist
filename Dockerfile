FROM ubuntu:latest AS build

RUN apt-get update && apt-get install -y openjdk-17-jdk && apt-get clean autoclean

COPY . .

RUN apt-get install maven -y && apt-get clean autoclean && mvn clean install

FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY --from=build /target/todoist-1.0.0.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
