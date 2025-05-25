# ---- STAGE 1: Build the JAR ----
FROM maven:3.8.6-openjdk-17-slim AS build

WORKDIR /app

# Copy only pom.xml first (to leverage Docker cache)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy the full source code
COPY src ./src

# Package the Spring Boot app
RUN mvn clean package -DskipTests

# ---- STAGE 2: Run the app ----
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Use dynamic port from Render
ENV PORT=8080
EXPOSE $PORT

# Start the app
CMD ["java", "-jar", "app.jar"]
