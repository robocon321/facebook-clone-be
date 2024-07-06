#Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim

# Install curl
RUN apt-get update && apt-get install -y curl && apt-get clean

#Information around who maintains the image
LABEL key=robocon321

# Add the application's jar to the image
COPY target/file-service-0.0.1-SNAPSHOT.jar app.jar

# execute the application
ENTRYPOINT ["java", "-jar", "app.jar"]