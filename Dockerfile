FROM openjdk:21

COPY ./target/main-0.0.1-SNAPSHOT.jar ./main-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar","main-0.0.1-SNAPSHOT.jar","--spring.profiles.active=dev"]