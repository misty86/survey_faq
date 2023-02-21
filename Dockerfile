FROM rasa/rasa:3.4.4-full
WORKDIR '/app'
COPY . /app
VOLUME /app
VOLUME /app/data
VOLUME /app/models
CMD ["run", "-m", "test.tar.gz","--enable-api"]

FROM openjdk:11-jdk-slim
EXPOSE 8080
ADD build/libs/app.jar /opt/jaicf/app.jar
ENTRYPOINT ["java", "-jar", "/opt/jaicf/app.jar"]
