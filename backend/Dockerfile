#### BUILD #################################
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copie le code source et le compile en un .jar
COPY src src
RUN mvn package


#### RUNTIME ###############################
FROM eclipse-temurin:17-jre
WORKDIR /app

# Récupère le .jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]