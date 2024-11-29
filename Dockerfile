FROM openjdk:21-jdk
EXPOSE 8080
COPY build/libs/CloudStorage-0.0.1-SNAPSHOT.jar app.jar
CMD ["java","-jar","app.jar"]