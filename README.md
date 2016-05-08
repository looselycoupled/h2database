# Overview

This repo contains code for the "Graph APIs in H2" CMSC-724 group project for Spring 2016.

# Organization

The h2 folder structure contains the modified H2 codebase to support graph API calls.  Our specific contributions can be found at `h2/src/main/org/h2/graph
`.  The includes all of the code that will parse the DSL, create the initial vertices, and allow for connecting the edge objects.

The cmsc724 codebase contains a Java application that embeds an H2 database to exercises the new graph API.  This can be thought of as "driver" code in order to verify our work and perform the experiments crucial to our final report.  The `cmsc724` directory contains a Makefile that was used to execute our tests/drivers during the long development phase of our project.  The specific experiment files that were reported on were `BFSExperimentH2.java` and `BFSExperimentSQL2.java`.  Other experiment files exist as well as former driver files for testing however some have become incompatible as our codebase evolved over time.  

# Execution

To re-run the report experiments we highly recommend using the included `Makefile` found in the `cmsc724` directory.  Both experiments we reported on make use of a Breadth First Search to find connected nodes in a graph.  

To execute the SQL based version you may use the following command:

    $ make bfssql2

To execute the graph API based version you may use the following command:

    $ make bfsh2

In order to ensure you are using the latest H2 codebase, you may want to rebuild the JAR files as required.  To do this, navigate to the `h2` folder at the top of the repo and execute the following script:

    $ ./build.sh jar

# Results

After executing the experiments, a report is generated to stdout after a series of logging messages.  Each line in the report is for a given iteration and takes the form of "nodeID, elapsedTime, nodesFound".  You may view the saved results of our experiments at `cmsc724/experimental_results`.
