# Use a small base image with Java 21
FROM eclipse-temurin:21-jdk-alpine

# Set working directory in the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/jobpilot.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
