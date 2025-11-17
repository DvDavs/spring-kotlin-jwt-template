# Etapa 1: Builder con Gradle preinstalado
FROM gradle:8.11.1-jdk17 AS builder
WORKDIR /app

# Argumento para usar el volumen cacheado
ARG GRADLE_USER_HOME=/cache
ENV GRADLE_USER_HOME=$GRADLE_USER_HOME

# Copiar archivos de configuración de Gradle
COPY build.gradle.kts settings.gradle.kts ./

# Descargar dependencias sin compilar código fuente
RUN gradle dependencies --no-daemon --parallel --build-cache

# Copiar el código fuente
COPY src src

# Construir el JAR sin clean para ser más rápido
RUN gradle bootJar --no-daemon --parallel --build-cache

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar JAR desde el builder
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 6003
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]

