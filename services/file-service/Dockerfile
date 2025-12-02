FROM common-lib:latest AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:24-jre-alpine-3.22
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/target/file-service.jar file-service.jar
USER app
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "file-service.jar"]
