FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY ./build/libs/*SNAPSHOT.jar project.jar
ENTRYPOINT ["java", "-jar", "/app/project.jar"]