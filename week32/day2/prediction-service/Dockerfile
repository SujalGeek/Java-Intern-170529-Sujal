FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
# Only install the light ML libraries needed for the Analytics models
RUN apt-get update && apt-get install -y python3 python3-pip && \
    pip3 install --break-system-packages scikit-learn pandas numpy joblib && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Copy the models so ModelLoader can find them
COPY src/main/resources/models/ ./models/

EXPOSE 8083
CMD ["java", "-jar", "app.jar"]
