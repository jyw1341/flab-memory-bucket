FROM openjdk:17-jdk

WORKDIR /re-memory

COPY build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/re-memory/app.jar"]
