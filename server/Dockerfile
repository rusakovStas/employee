FROM openjdk:11-jdk
COPY ./build/libs/backend-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","backend-0.0.1-SNAPSHOT.jar"]