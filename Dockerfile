FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/tourguide-0.0.1-SNAPSHOT.jar /app/tourguide.jar

ENTRYPOINT ["java", "-jar", "tourguide.jar"]