# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy all project files
COPY . .

# Build the Spring Boot application
RUN mvn clean package -DskipTests


# ---------- RUN STAGE ----------
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/mentors-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render will override using PORT env)
EXPOSE 8080

# Run application
ENTRYPOINT ["java","-jar","app.jar"]
