#!/bin/bash
# OpenTelemetry Java Agent 다운로드

OTEL_VERSION="2.12.0"
JAR_NAME="opentelemetry-javaagent.jar"

if [ -f "$JAR_NAME" ]; then
    echo "OTel Agent already exists: $JAR_NAME"
    exit 0
fi

echo "Downloading OTel Java Agent v${OTEL_VERSION}..."
curl -L -o "$JAR_NAME" \
  "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_VERSION}/opentelemetry-javaagent.jar"

echo "Download complete: $JAR_NAME"
