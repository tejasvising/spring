# ---- STAGE 1: Build the JAR ----
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy pom and download dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build the app
COPY src ./src
RUN mvn clean package -DskipTests

# ---- STAGE 2: Run the app ----
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Let Render inject the port
ENV PORT=8080
EXPOSE $PORT

CMD ["java", "-jar", "app.jar"]
