SHELL := /bin/sh

LOCALPATH := $(CURDIR)

JARPATHS = ".:./src:../h2/bin/h2-1.4.191.jar:../h2/ext/blueprints-core-2.6.0.jar"
# JVMMEMOPTS = -Xms4096m -Xmx6144m
JVMMEMOPTS = -Xms6144m -Xmx8192m

.PHONY: clean build

testdsl:
	javac -cp $(JARPATHS) ./src/TestDSLGraphDefinition.java

testgraphdef:
	javac -cp $(JARPATHS) ./src/TestGraphDefinition.java
	java -cp $(JARPATHS) TestGraphDefinition

testdriver:
	javac -cp $(JARPATHS) ./src/TestDriver.java
	java -cp $(JARPATHS) TestDriver


testgraphregister:
	javac -cp $(JARPATHS) ./src/TestGraphRegister.java
	java -cp $(JARPATHS) TestGraphRegister


bfssql:
	javac -cp $(JARPATHS) ./src/BFSExperimentSQL.java
	java -cp $(JARPATHS) $(JVMMEMOPTS) BFSExperimentSQL

bfssql2:
	javac -cp $(JARPATHS) ./src/BFSExperimentSQL2.java
	java -cp $(JARPATHS) $(JVMMEMOPTS) BFSExperimentSQL2


bfsh2:
	javac -cp $(JARPATHS) ./src/BFSExperimentH2.java
	java -cp $(JARPATHS) $(JVMMEMOPTS) BFSExperimentH2


dfssql:
	javac -cp $(JARPATHS) ./src/DFSExperimentSQL.java
	java -cp $(JARPATHS) $(JVMMEMOPTS) DFSExperimentSQL


randvsql:
	javac -cp $(JARPATHS) ./src/RandomVertexExperimentSQL.java
	java -cp $(JARPATHS) $(JVMMEMOPTS) RandomVertexExperimentSQL

# Clean build files
clean:
	find . -name "*.class" -print0 | xargs -0 rm -rf
