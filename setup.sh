#!/bin/bash

ERROR='\033[0;31m'
SUCCESS='\033[0;32m'
WARNING='\033[0;33m'
RESET='\033[0m'

echo -e "${WARNING}Docker compose up mysql database${RESET}"
sudo docker compose -f docker-compose/docker-compose.yml up -d mysql-db


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

echo -e "${WARNING}Build facebook-clone-be image${RESET}"
docker_build_command="sudo docker build -t robocon321/facebook-clone-be:latest ."
$docker_build_command

if [ $? -eq 1 ]; then
    echo -e "${ERROR}Build facebook-clone-be fail${RESET}"
    exit 1;
fi

echo -e "${WARNING}Docker build completed!${RESET}"

echo -e "${WARNING}Docker push image to dockerhub${RESET}"
sudo docker image push robocon321/facebook-clone-be:latest

echo -e "${WARNING}Run file docker-compose to start system${RESET}"
sudo docker compose -f docker-compose/docker-compose.yml up -d

exit 0;