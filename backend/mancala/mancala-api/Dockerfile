FROM openjdk:17-alpine
WORKDIR /opt/app
ARG JAR_FILE=build/libs/mancala-api-0.1.jar
COPY ${JAR_FILE} mancala-api.jar
EXPOSE 8080
ENV DB_HOST=dbpos
ENV DB_NAME=postgres
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=postgres
ENTRYPOINT ["java","-jar","mancala-api.jar"]
