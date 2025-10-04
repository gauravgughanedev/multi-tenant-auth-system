FROM docker.io/maven:3.9.11-amazoncorretto-17-alpine AS build

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM docker.io/amazoncorretto:17-alpine3.20-jdk

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build --chown=appuser:appgroup /build/target/*.jar app.jar

USER appuser

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]