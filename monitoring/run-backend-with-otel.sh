#!/bin/bash
set -euo pipefail
# OTel Agent와 함께 백엔드 실행

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# .env 파일 로드 (backend/.env 사용)
BACKEND_ENV="$SCRIPT_DIR/../backend/.env"
if [ -f "$BACKEND_ENV" ]; then
    echo "Loading backend/.env..."
    set -a
    source "$BACKEND_ENV"
    set +a
else
    echo "Error: backend/.env not found. Run 'make setup' first."
    exit 1
fi
BACKEND_DIR="$SCRIPT_DIR/../backend"
OTEL_AGENT="$SCRIPT_DIR/otel-agent/opentelemetry-javaagent.jar"
OTEL_CONFIG="$SCRIPT_DIR/otel-agent/otel.properties"

# OTel Agent 확인
if [ ! -f "$OTEL_AGENT" ]; then
    echo "OTel Agent not found. Downloading..."
    cd "$SCRIPT_DIR/otel-agent" && ./download.sh
fi

# 백엔드 JAR 찾기
BACKEND_JAR=""
if [ -d "$BACKEND_DIR/build/libs" ]; then
    BACKEND_JAR=$(find "$BACKEND_DIR/build/libs" -name "*.jar" -not -name "*-plain.jar" | head -1)
fi

if [ -z "$BACKEND_JAR" ]; then
    echo "Backend JAR not found. Building..."
    cd "$BACKEND_DIR" && ./gradlew build -x test
    BACKEND_JAR=$(find "$BACKEND_DIR/build/libs" -name "*.jar" -not -name "*-plain.jar" | head -1)
fi

if [ -z "$BACKEND_JAR" ]; then
    echo "Error: Backend JAR not found"
    exit 1
fi

echo "Starting backend with OTel Agent..."
echo "Backend JAR: $BACKEND_JAR"
echo "OTel Agent: $OTEL_AGENT"
echo "OTel Config: $OTEL_CONFIG"

# 환경변수로 JSON 로깅 활성화
export LOG_FORMAT=logstash

java -javaagent:"$OTEL_AGENT" \
  -Dotel.javaagent.configuration-file="$OTEL_CONFIG" \
  -jar "$BACKEND_JAR"
