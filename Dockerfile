# ============================
# Stage 1: Build the application
# ============================
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set working directory inside the image
WORKDIR /app

# Copy pom.xml and download dependencies (for better build caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the application JAR (skip tests for faster build)
RUN mvn clean package -DskipTests

# ============================
# Stage 2: Create runtime image
# ============================
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy only the final JAR from build stage
COPY --from=build /app/target/*.jar app.jar



# Expose the application port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]

