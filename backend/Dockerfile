# ---- Etap budowania ----
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Pobierz zależności (cache layer) i zbuduj
RUN apk add --no-cache maven && \
    mvn dependency:go-offline -B && \
    mvn package -DskipTests -B

# ---- Etap produkcyjny ----
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Utwórz niepriwilegowanego użytkownika
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
