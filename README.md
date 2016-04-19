# Overview

This repo contains code for the "Graph APIs in H2" CMSC-724 group project for Spring 2016.

# Organization

The h2 folder structure contains the modified H2 codebase to support graph API calls.

The cmsc724 codebase contains a Java application that embeds an H2 database to exercise the new graph API.

# Execution

To build/run the project codebase, navigate to the cmsc724 folder and run the following from the command line:

    $ make run

The Makefile has instructions to build the TestDriver class and will then immediately execute it.
