
FROM eclipse-temurin:21-jdk as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/postService-0.0.1-SNAPSHOT.jar ./app.jar
# 👇 limit max heap size to 256MB
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]