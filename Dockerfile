FROM alpine:3.20 AS builder

WORKDIR /build
COPY . .

RUN apk add openjdk21 maven make && \
    make build

FROM alpine:3.20
LABEL org.opencontainers.image.source=https://github.com/PlanningTCG/planning-poker

WORKDIR /app
COPY --from=builder /build/target/Planning-Poker*.jar planningpoker.jar

RUN apk add --no-cache openjdk21-jre-headless
EXPOSE 5000
ENTRYPOINT [ "java", "-jar", "./planningpoker.jar" ]
