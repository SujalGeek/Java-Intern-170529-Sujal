# Use a lightweight Java image
FROM eclipse-temurin:21-jdk-alpine

# We work in a temp directory
VOLUME /tmp

# Copy the JAR built by your Windows IDE (or Maven) into the container
# This path works fine even if the file technically sits on Windows
COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]