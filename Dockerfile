# ==========================================
# Stage 1: Build Stage
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy Maven wrapper and POM first to leverage Docker layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies offline (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code and package application (skip tests as CI/DevOps validates them separately)
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ==========================================
# Stage 2: Runtime Stage
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Create a non-root user and group for security compliance
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy only the compiled fat JAR from the builder stage
COPY --from=builder --chown=spring:spring /build/target/*.jar app.jar

# Expose default application port
EXPOSE 8080

# Configure production-ready JVM memory flags and entrypoint
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]