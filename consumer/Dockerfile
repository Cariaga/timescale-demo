FROM openjdk:8-jre-slim

COPY target/consumer-*-microbundle.jar /app/app.jar
WORKDIR /app
CMD ["java", "-jar", "app.jar"]
