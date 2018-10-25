FROM openjdk:8-jre-slim

COPY target/producer-*.jar /app/app.jar
WORKDIR /app
CMD ["java", "-jar", "app.jar"]
