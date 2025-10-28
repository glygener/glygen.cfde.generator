# Stage 1: Build the application
FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /app
COPY ./pom.xml .
COPY ./src ./src
RUN mvn clean package -DskipTests
RUN mvn dependency:copy-dependencies -DoutputDirectory=./target/libs/

# Stage 2: Create the final runtime image
FROM openjdk:21
WORKDIR /app
RUN mkdir -p libs
RUN mkdir -p data
COPY --from=build /app/target/*.jar libs/
COPY --from=build /app/target/libs/*.jar libs/
CMD ["java", "-cp", "/app/libs/*", "org.glygen.cfde.generator.App", "-c", "/app/data/files.csv", "-o", "/app/data/output/", "-p", "/app/data/glygen.properties", "-m", "/app/data/mapping/" "-g"]