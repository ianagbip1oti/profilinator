FROM openjdk:11 AS build
WORKDIR /var/src

# Download gradle
COPY gradlew .
COPY gradle/ gradle/
RUN ./gradlew --no-daemon --version

# Build
COPY . .
RUN ./gradlew --no-daemon shadowJar

FROM openjdk:11-jre
WORKDIR /var/app
RUN mkdir /var/data

ENV PROFILINATOR_DB_PATH=/var/data

COPY --from=build /var/src/build/libs/profilinator-all.jar .

CMD java -jar profilinator-all.jar
