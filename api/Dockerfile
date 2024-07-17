FROM gradle:8.8.0-jdk17 as build
WORKDIR /app
COPY build.gradle .
COPY src /app/src
RUN gradle clean bootJar

FROM eclipse-temurin:17.0.11_9-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

