FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

RUN apt-get update && \
    apt-get install -y libgl1 libgtk-3-0 libxext6 libxrender1 libxtst6 libxi6 xvfb && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/target/daa-assignment-1.0-SNAPSHOT.jar app.jar
COPY src/main/resources/input.json src/main/resources/input.json

RUN mkdir -p output/graphs

ENV DISPLAY=:99

CMD ["sh", "-c", "Xvfb :99 -screen 0 1920x1080x24 & sleep 2 && java -jar app.jar src/main/resources/input.json output/output.json output/graphs"]