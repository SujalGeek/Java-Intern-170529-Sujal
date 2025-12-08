# ===== Build Stage =====
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -e -X -DskipTests clean package

# ===== Run Stage =====
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
