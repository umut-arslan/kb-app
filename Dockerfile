FROM openjdk:17-jdk
COPY backend_spring/target/backend_spring-1.0-SNAPSHOT.jar /usr/src/entwicklungstemplate.jar
WORKDIR /usr/src/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "entwicklungstemplate.jar"]