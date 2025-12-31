FROM eclipse-temurin:21-jdk

COPY ./target/main-0.0.1-SNAPSHOT.jar ./main-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar","main-0.0.1-SNAPSHOT.jar"]

CMD ["--spring.profiles.active=dev"]
