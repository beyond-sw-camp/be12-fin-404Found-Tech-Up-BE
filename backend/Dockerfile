FROM    openjdk:17-ea-slim-buster
EXPOSE  8080
COPY     ./build/libs/backend-0.0.1-SNAPSHOT.jar  /app.jar
ENTRYPOINT     ["java", "-jar", "/app.jar"]

# CMD는 compose-file에서 command로 지정한 것으로 대체됨
# ENTRYPOINT는 먼저 실행되고 command가 실행됨