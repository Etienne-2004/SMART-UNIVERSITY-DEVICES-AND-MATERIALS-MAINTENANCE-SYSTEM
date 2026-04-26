#!/bin/sh
# ----------------------------------------------------------------------------
# Simplified Maven Wrapper for Linux/macOS/GitBash
# ----------------------------------------------------------------------------

MAVEN_PROJECTBASEDIR=$(dirname "$0")
CLASSPATH="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"

if [ ! -f "$CLASSPATH" ]; then
    echo "📥 Downloading Maven Wrapper..."
    mkdir -p "$MAVEN_PROJECTBASEDIR/.mvn/wrapper"
    curl -sfL https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar -o "$CLASSPATH"
fi

java -classpath "$CLASSPATH" org.apache.maven.wrapper.MavenWrapperMain "$@"
