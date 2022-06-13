#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY pom.xml /opt/
COPY src /opt/src/
WORKDIR /opt
RUN mvn -f /opt/pom.xml clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /opt/target/*.jar /usr/local/lib/app.jar
ENV PORT 3307
EXPOSE 3307
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]