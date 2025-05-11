# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/app.jar app.jar

# Expose the port your application runs on
EXPOSE 8010

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]