FROM maven:3.9.6-amazoncorretto-21-debian-bookworm as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
FROM openjdk:11
COPY ./target/ucj-1.jar app.jar
EXPOSE 81
CMD ["java", "-jar", "app.jar"]