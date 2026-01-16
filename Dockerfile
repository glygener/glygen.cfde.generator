
ARG IMAGE_VERSION
ARG IMAGE_CREATED

##########################################
# Stage 1: Build the application
##########################################
FROM maven:3.8.7-openjdk-18 AS build
# create working directory and copy maven file and source code
WORKDIR /app
COPY ./pom.xml .
COPY ./src ./src
# compile the source code and copy all dependencies into a separate directory 
RUN mvn clean package -DskipTests
RUN mvn dependency:copy-dependencies -DoutputDirectory=./target/libs/

##########################################
# Stage 2: Create the final runtime image
##########################################
FROM eclipse-temurin:25
# metadata
LABEL org.opencontainers.image.authors="Rene Ranzinger, Complex Carbohydrate Research Center, University of Georgia" \
      org.opencontainers.image.title="GlyGen C2M2 Metadata Generator" \
      org.opencontainers.image.description="Java application for generating CFDE C2M2 data from GlyGen and GlyGen Glycan Array Data Repository" \
      org.opencontainers.image.licenses="GPL" \
      org.opencontainers.image.source="https://github.com/glygener/glygen.cfde.generator" \
      org.opencontainers.image.version="${IMAGE_VERSION}" \
      org.opencontainers.image.created="${IMAGE_CREATED}" \
      org.opencontainers.image.url="https://hub.docker.com/repository/docker/glygen/c2m2-generator/"
# create working directory and subdirectories
WORKDIR /app
RUN mkdir -p libs
RUN mkdir -p data
# copy compiled application and dependencies 
COPY --from=build /app/target/*.jar libs/
COPY --from=build /app/target/libs/*.jar libs/
# create non-root user, assign file to the user and switch to this user
RUN adduser -D glygen && chown -R glygen /app
USER glygen
# run the application
CMD ["java", "-cp", "/app/libs/*", "org.glygen.cfde.generator.App", "-c", "/app/data/files.csv", "-o", "/app/data/output/", "-p", "/app/data/glygen.properties", "-m", "/app/data/mapping/" "-g"]
