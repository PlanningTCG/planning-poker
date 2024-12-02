# Function to convert a string to lowercase
lc = $(subst A,a,$(subst B,b,$(subst C,c,$(subst D,d,$(subst E,e,$(subst F,f,$(subst G,g,$(subst H,h,$(subst I,i,$(subst J,j,$(subst K,k,$(subst L,l,$(subst M,m,$(subst N,n,$(subst O,o,$(subst P,p,$(subst Q,q,$(subst R,r,$(subst S,s,$(subst T,t,$(subst U,u,$(subst V,v,$(subst W,w,$(subst X,x,$(subst Y,y,$(subst Z,z,$1))))))))))))))))))))))))))

JAR_NAME = $(shell mvn help:evaluate -Dexpression=project.build.finalName -q -DforceStdout)

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
	docker build -t $(call lc,$(JAR_NAME)) .

run-docker:
	@echo "Running Docker container..."
	docker run -p 5000:5000 -it --rm $(call lc,$(JAR_NAME))

.PHONY:
	debug watch build run build-run clean
