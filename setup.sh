#!/bin/bash

ERROR='\033[0;31m'
SUCCESS='\033[0;32m'
WARNING='\033[0;33m'
RESET='\033[0m'

echo -e "${WARNING}Maven test and build artifact${RESET}"
if ! command -v mvn > /dev/null 2>&1; then
    echo -e "${ERROR}Maven is not installed. Please install docker and try again${RESET}"
    exit 1;
fi

sudo docker info >/dev/null 2>&1

if [ $? -ne 0 ]
then
  echo -e "${ERROR}Please login docker. Use 'docker login' command to log into your dockerhub account${RESET}"
  exit 1
fi

mvn clean install

echo -e "${WARNING}Docker build${RESET}"

if ! command -v sudo docker > /dev/null 2>&1; then
    echo -e "${ERROR}Docker is not installed. Please install docker and try again${RESET}"
    exit 1
fi

echo -e "${WARNING}Build account-service image${RESET}"
cd account-service
docker_build_command="sudo docker build -t robocon321/account-service:latest ."
$docker_build_command

if [ $? -eq 1 ]; then
    echo -e "${ERROR}Build account-service fail${RESET}"
    exit 1;
fi

echo -e "${WARNING}Build api-gateway image${RESET}"
cd ..
cd api-gateway
docker_build_command="sudo docker build -t robocon321/api-gateway:latest ."
$docker_build_command

if [ $? -eq 1 ]; then
    echo -e "${ERROR}Build api-gateway fail${RESET}"
    exit 1;
fi

echo -e "${WARNING}Build auth-service image${RESET}"
cd ..
cd auth-service
docker_build_command="sudo docker build -t robocon321/auth-service:latest ."
$docker_build_command

if [ $? -eq 1 ]; then
    echo -e "${ERROR}Build auth-service fail${RESET}"
    exit 1;
fi

echo -e "${WARNING}Build coverage-report image${RESET}"
cd ..
cd coverage-report
docker_build_command="sudo docker build -t robocon321/coverage-report:latest ."
$docker_build_command

if [ $? -eq 1 ]; then
    echo -e "${ERROR}Build coverage-report fail${RESET}"
    exit 1;
fi

echo -e "${WARNING}Build discovery-server image${RESET}"
cd ..
cd discovery-server
docker_build_command="sudo docker build -t robocon321/discovery-server:latest ."
$docker_build_command

if [ $? -eq 1 ]; then
    echo -e "${ERROR}Build discovery-server fail${RESET}"
    exit 1;
fi

echo -e "${WARNING}Build file-service image${RESET}"
cd ..
cd file-service
docker_build_command="sudo docker build -t robocon321/file-service:latest ."
$docker_build_command

if [ $? -eq 1 ]; then
    echo -e "${ERROR}Build file-service fail${RESET}"
    exit 1;
fi

echo -e "${WARNING}Build location-service image${RESET}"
cd ..
cd location-service
docker_build_command="sudo docker build -t robocon321/location-service:latest ."
$docker_build_command

if [ $? -eq 1 ]; then
    echo -e "${ERROR}Build location-service fail${RESET}"
    exit 1;
fi

echo -e "${WARNING}Build post-service image${RESET}"
cd ..
cd post-service
docker_build_command="sudo docker build -t robocon321/post-service:latest ."
$docker_build_command

if [ $? -eq 1 ]; then
    echo -e "${ERROR}Build post-service fail${RESET}"
    exit 1;
fi

echo -e "${WARNING}Build realtime-service image${RESET}"
cd ..
cd realtime-service
docker_build_command="sudo docker build -t robocon321/realtime-service:latest ."
$docker_build_command

if [ $? -eq 1 ]; then
    echo -e "${ERROR}Build realtime-service fail${RESET}"
    exit 1;
fi

echo -e "${WARNING}Docker build completed!${RESET}"

cd ..

echo -e "${WARNING}Docker push image to dockerhub${RESET}"
sudo docker image push robocon321/account-service:latest
sudo docker image push robocon321/api-gateway:latest
sudo docker image push robocon321/auth-service:latest
sudo docker image push robocon321/coverage-report:latest
sudo docker image push robocon321/discovery-server:latest
sudo docker image push robocon321/file-service:latest
sudo docker image push robocon321/location-service:latest
sudo docker image push robocon321/post-service:latest
sudo docker image push robocon321/realtime-service:latest

echo -e "${WARNING}Run file docker-compose to start system${RESET}"
sudo docker compose -f docker-compose/docker-compose.yml up -d

exit 0;