FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN mkdir -p /app/data && chgrp -R 0 /app && chmod -R g=u /app
COPY target/benachrichtigung-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
