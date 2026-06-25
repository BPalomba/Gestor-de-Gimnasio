FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar el jar generado por Maven
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]