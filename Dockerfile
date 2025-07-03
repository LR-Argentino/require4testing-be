# Multi-stage build für optimierte Image-Größe
FROM ghcr.io/graalvm/graalvm-ce:24-ol9 AS builder

# Arbeitsverzeichnis setzen
WORKDIR /app

# Maven Wrapper und pom.xml kopieren für besseres Caching
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Dependencies herunterladen (wird gecacht, solange sich pom.xml nicht ändert)
RUN ./mvnw dependency:go-offline -B

# Source Code kopieren
COPY src src

# Application bauen (Tests überspringen für schnelleres Build)
RUN ./mvnw clean package -DskipTests -B

# Runtime Stage - kleineres Image
FROM ghcr.io/graalvm/graalvm-ce:24-ol9

# Curl für Health Check installieren
RUN microdnf install -y curl && microdnf clean all

# Non-root user für Sicherheit
RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -m appuser

# Arbeitsverzeichnis erstellen
WORKDIR /app

# JAR aus Builder Stage kopieren
COPY --from=builder /app/target/*.jar app.jar

# Ownership ändern
RUN chown appuser:appgroup app.jar

# User wechseln
USER appuser

# Port exponieren
EXPOSE 8080

# Health Check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Application starten
ENTRYPOINT ["java", "-jar", "app.jar"]