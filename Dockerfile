# Etapa 1: Build con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ligera con JDK
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Variables necesarias para Cloud Run
ENV PORT=8080

# Copia el jar construido
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
