FROM common-lib:latest AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:24-jre-alpine-3.22
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/target/notification-service.jar notification-service.jar
USER app
ENTRYPOINT ["java", "-jar", "notification-service.jar"]