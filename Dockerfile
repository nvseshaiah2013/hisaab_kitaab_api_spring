# our base build image
FROM maven:3.9.6-amazoncorretto-17-al2023 as maven

# copy the project files
COPY ./pom.xml ./pom.xml

# build all dependencies
RUN mvn dependency:go-offline -B

# copy your other files
COPY ./src ./src

# build for release
RUN mvn package -DskipTests

# our final base image
FROM amazoncorretto:17.0.10-al2023-headless

# Add app user
ARG APPLICATION_USER=100

# Configure working directory
RUN mkdir /app && \
    chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY --chown=1000:1000 --from=maven ./*.jar /app/app.jar

#set working directory
WORKDIR /app

#expose application port
EXPOSE 3000

# set the startup command to run your binary
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]