# Etapa de construcción
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa final
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar el jar construido
COPY --from=build /app/target/muniproject-0.0.1-SNAPSHOT.jar app.jar

# Definir el perfil por defecto (puedes cambiarlo en docker run o docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod

# Arrancar la app con el perfil definido
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=$PORT"]


