FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Kopiujemy pliki projektu (w tym pom.xml i src/)
COPY . .

# Budujemy .jar (skipTests opcjonalnie)
RUN mvn clean package -DskipTests

# Finalny obraz z JDK (mniejszy)
FROM openjdk:17-jdk-slim
WORKDIR /app

# Kopiujemy tylko .jar z poprzedniego stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]