SHELL := /bin/sh

LOCALPATH := $(CURDIR)

.PHONY: clean build

build:
	javac -cp ".:./src:../h2/bin/h2-1.4.191.jar" ./src/TestDriver.java

run:
	javac -cp ".:./src:../h2/bin/h2-1.4.191.jar" ./src/TestDriver.java
	java -cp ".:./src:../h2/bin/h2-1.4.191.jar" TestDriver

# Clean build files
clean:
	find . -name "*.class" -print0 | xargs -0 rm -rf