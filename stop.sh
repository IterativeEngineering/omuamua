#!/bin/bash

# Stop the application

echo "Stopping the application..."
cd docker
docker-compose -f docker-compose-app.yml down

echo "Application stopped."