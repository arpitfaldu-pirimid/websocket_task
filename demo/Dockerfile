# =========================
# Build Stage
# =========================
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Copy maven config first (cache deps)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

# Copy source
COPY src ./src

# Build jar
RUN ./mvnw clean package -DskipTests


# =========================
# Runtime Stage
# =========================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Security: non-root user
RUN useradd -r spring

# Copy jar from build stage
COPY --from=builder /app/target/*.jar app.jar

RUN chown spring:spring app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
