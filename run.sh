#!/bin/bash

ERROR='\033[0;31m'
SUCCESS='\033[0;32m'
WARNING='\033[0;33m'
RESET='\033[0m'

echo -e "${WARNING}Docker run all application${RESET}"

java -jar discovery-server.jar &
java -jar api-gateway.jar &
java -jar auth-service.jar &
java -jar account-service.jar &
java -jar file-service.jar &
java -jar location-service.jar &
java -jar post-service.jar &
java -jar realtime-service.jar &

wait

echo -e "${WARNING}Done${RESET}"