# Use a small base image with Java 17
FROM openjdk:17-jdk-slim

# Set working directory in the container
WORKDIR /app

# Copy the built JAR file from your local machine into the container
COPY target/jobpilot.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
