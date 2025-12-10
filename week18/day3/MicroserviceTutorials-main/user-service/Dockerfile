# ---------- BUILDER STAGE ----------
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy only pom.xml first (cache dependencies)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn -DskipTests clean package


# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (your service runs on 8083)
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
