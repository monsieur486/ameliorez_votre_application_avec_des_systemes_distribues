# Étape 1 : build avec Maven
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : image d'exécution minimale
FROM openjdk:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
EXPOSE 8080
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
