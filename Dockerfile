FROM amazoncorretto:17 as temp_build_image
ENV APP_HOME=/discordbase
WORKDIR $APP_HOME
COPY build.gradle settings.gradle gradlew $APP_HOME/
COPY gradle $APP_HOME/gradle
RUN ./gradlew bootJar 2>/dev/null || true
COPY . .
RUN ./gradlew bootJar

FROM amazoncorretto:17
ENV ARTIFACT_NAME=lifebot-1.0-SNAPSHOT.jar
ENV APP_HOME=/discordbase
WORKDIR $APP_HOME
COPY --from=temp_build_image $APP_HOME/build/libs/$ARTIFACT_NAME $APP_HOME/build/libs/$ARTIFACT_NAME
ENV TZ="America/Denver"
ENV PROD="true"

CMD java -jar /discordbase/build/libs/$ARTIFACT_NAME
