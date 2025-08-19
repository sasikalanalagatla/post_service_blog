FROM eclipse-temurin:21-jdk as builder
WORKDIR /app

# Copy Maven wrapper and config
COPY mvnw .
COPY .mvn/ .mvn

# Copy pom.xml
COPY pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build project
RUN ./mvnw clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/postService-0.0.1-SNAPSHOT.jar ./app.jar

# Limit max heap size
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]