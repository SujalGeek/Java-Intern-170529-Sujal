# Stage 1: Build with Maven and Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with lightweight Java 21 Alpine JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# 🔥 CHANGE THIS PORT FOR EACH SERVICE
EXPOSE 8082 

CMD ["java", "-jar", "app.jar"]
