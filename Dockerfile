FROM maven:latest AS builder

WORKDIR /build
COPY . .

RUN mvn compile assembly:single

FROM openjdk:21-jdk-slim
LABEL org.opencontainers.image.source=https://github.com/PlanningTCG/planning-poker

WORKDIR /app
COPY --from=builder /build/target/Planning-Poker*-with-dependencies.jar planningpoker.jar

EXPOSE 5000
ENTRYPOINT [ "java", "-jar", "./planningpoker.jar" ]
