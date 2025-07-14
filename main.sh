#!/bin/bash
#!/bin/bash
set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Start"
docker compose -f oumuamua-postgresql/docker-compose.yml up pg-main -d

./gradlew clean bootRun --args='--spring.profiles.active=main'


# Build the frontend
echo "Building frontend..."
cd frontend
chmod +x build.sh
./build.sh
cd ..

# Start the application with Docker Compose
echo "Starting the application..."
cd docker
docker-compose -f docker-compose-app.yml up -d

echo "Application is running!"
echo "Frontend: http://localhost:3000"
echo "oumuamua-backend API: http://localhost:8080/api/users"