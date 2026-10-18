#!/bin/bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JDK="/home/fengye/mc-adventure/jdk17/bin"

CP="/home/fengye/mc-mod-dev/cache/client-intermediary.jar"
CP="$CP:/home/fengye/mc-adventure/libraries/net/fabricmc/fabric-loader/0.16.14/fabric-loader-0.16.14.jar"
CP="$CP:/home/fengye/mc-adventure/mods/fabric-api-0.92.12+1.20.1.jar"

for f in /home/fengye/mc-mod-dev/cache/extracted_jars/*.jar; do
    CP="$CP:$f"
done
for f in $(find /home/fengye/mc-adventure/libraries -name "*.jar"); do
    CP="$CP:$f"
done

mkdir -p "$DIR/build/classes"
rm -rf "$DIR/build/classes/*"

echo "Compiling Java sources..."
"$JDK/javac" -encoding UTF-8 -cp "$CP" -d "$DIR/build/classes" $(find "$DIR/src/main/java" -name "*.java")

echo "Copying resources..."
cp -r "$DIR/src/main/resources/"* "$DIR/build/classes/"

mkdir -p "$DIR/build/libs"
JAR="$DIR/build/libs/easy-villagers-fabric-1.0.0.jar"
echo "Creating jar: $JAR"
"$JDK/jar" -cf "$JAR" -C "$DIR/build/classes" .

echo "Build successful: $JAR"
