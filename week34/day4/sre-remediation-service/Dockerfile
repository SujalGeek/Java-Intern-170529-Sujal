# Step 1: Build stage (Optional if you use local JAR)
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
# Copy the JAR from your target folder
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
