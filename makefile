JAR_NAME = $(shell mvn help:evaluate -Dexpression=project.build.finalName -q -DforceStdout)
JAR_NAME_LC  = $(shell echo $(JAR_NAME) | tr '[:upper:]' '[:lower:]')

# Default target
debug:
	@echo "Starting the application..."
	mvn exec:java

# SHR - https://github.com/0x6DD8/simple-hot-reload
watch:
	@echo "Starting the application with hot reload..."
	shr ./src mvn compile exec:java

# Build the application
build:
	mvn clean compile assembly:single
	mv ./target/*jar-with-dependencies.jar ./target/$(JAR_NAME).jar

# Run the application
run:
	java -jar ./target/$(JAR_NAME).jar

test:
	mvn test

build-run: build run

# Clean up build artifacts
clean:
	rm -fr ./target
	mvn clean

build-docker:
	@echo "Building Docker image..."
	docker build -t $(JAR_NAME_LC) .

run-docker:
	@echo "Running Docker container..."
	docker run -p 5000:5000 -it --rm $(JAR_NAME_LC)

.PHONY:
	debug watch build run build-run clean
