FROM alpine:3.20
LABEL org.opencontainers.image.source=https://github.com/0x6DD8/planning-poker

WORKDIR /build
COPY . .
RUN apk update && \
    apk add openjdk21 maven make && \
    make build && \
    mkdir /app && \
    mv /build/target/Planning-Poker*.jar /app/planningpoker.jar && \
    rm -rf /build && \
    apk del maven make && \
    apk cache clean

# Start container
WORKDIR /app
EXPOSE 5000
ENTRYPOINT [ "java", "-jar", "./planningpoker.jar" ]
