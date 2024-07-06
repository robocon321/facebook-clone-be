#Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim

# Setup working directory
WORKDIR /app

# Install curl
RUN apt-get update && apt-get install -y curl && apt-get clean

#Information around who maintains the image
LABEL key=robocon321

# Add the application's jar to the image
COPY discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar discovery-server.jar
COPY api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar
COPY auth-service/target/auth-service-0.0.1-SNAPSHOT.jar auth-service.jar
COPY account-service/target/account-service-0.0.1-SNAPSHOT.jar account-service.jar
COPY file-service/target/file-service-0.0.1-SNAPSHOT.jar file-service.jar
COPY location-service/target/location-service-0.0.1-SNAPSHOT.jar location-service.jar
COPY post-service/target/post-service-0.0.1-SNAPSHOT.jar post-service.jar
COPY realtime-service/target/realtime-service-0.0.1-SNAPSHOT.jar realtime-service.jar
COPY run.sh .

# add execute permission
RUN chmod +x run.sh

# execute the application
ENTRYPOINT ["./run.sh"]