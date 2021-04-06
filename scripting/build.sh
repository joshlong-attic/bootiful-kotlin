#!/usr/bin/env bash

# KOTLIN_HOME=$HOME/.sdkman/candidates/kotlin/current/
SCRIPT=hello.kts
JAR_NAME=hello.jar
STDLIB_JAR="$KOTLIN_HOME/lib/kotlin-stdlib-jdk8.jar:$KOTLIN_HOME/lib/kotlin-compiler.jar"


# echo "1: THE LONG WAY AROUND"
# kotlinc-jvm $SCRIPT -d $JAR_NAME
# java -cp $JAR_NAME:$STDLIB_JAR Hello

echo "2: KOTLIN "
kotlin -cp $JAR_NAME Hello 

# echo "3: KOTLIN SCRIPTING"
# kotlinc -script $SCRIPT