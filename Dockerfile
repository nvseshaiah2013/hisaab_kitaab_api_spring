## Start Building image in a container
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
## End building image in container
## Comment above part if running application in IDE and JAR is already created

# our final base image
FROM amazoncorretto:17.0.10-al2023-headless

# Add app user
ARG APPLICATION_USER=1000

# Configure working directory
RUN mkdir /app && chown -R $APPLICATION_USER /app

#Set User
USER $APPLICATION_USER

# copy from maven image
COPY --from=maven target/*.jar /app/app.jar

###Uncomment when running in IDE
##copy from local instance
#COPY target/*.jar /app/app.jar


#set working directory
WORKDIR /app

#expose application port
EXPOSE 3080

# set the startup command to run your binary
ENTRYPOINT [ "java", "-jar", "app.jar" ]