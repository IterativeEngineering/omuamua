FROM gradle:7.6.1-jdk17 as build
WORKDIR /app

# Copy build files
COPY build.gradle settings.gradle ./
COPY src ./src

# Build the application
RUN gradle build --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
