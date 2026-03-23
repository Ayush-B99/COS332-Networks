#!/bin/bash
# Wrapper script for Java CGI program

# Set error handling
set -e

# Define paths
JAVA_CMD="/usr/bin/java"
CLASS_PATH="/Users/ayush/COS332/Prac1/classes"
MAIN_CLASS="NumberGame"

# Execute Java program
exec $JAVA_CMD -cp $CLASS_PATH $MAIN_CLASS
