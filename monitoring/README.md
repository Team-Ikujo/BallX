# BallX Monitoring Stack

로컬 개발 환경을 위한 모니터링 스택입니다.

## 구성 요소

| 서비스 | 포트 | 설명 |
|--------|------|------|
| Grafana | 9000 | 대시보드 (admin/admin) |
| Loki | 3100 | 로그 저장소 |
| Tempo | 3200 | 트레이스 저장소 |
| OTel Collector | 4317, 4318 | 텔레메트리 수집 |

## 실행 방법

```bash
cd monitoring
docker compose up -d
```

## 접속

- Grafana: http://localhost:9000 (admin / admin)

## 백엔드 연동

### 1. OTel Agent 다운로드

```bash
cd monitoring/otel-agent
./download.sh
```

### 2. 백엔드 실행

```bash
java -javaagent:monitoring/otel-agent/opentelemetry-javaagent.jar \
  -Dotel.javaagent.configuration-file=monitoring/otel-agent/otel.properties \
  -jar backend.jar
```

## 종료

```bash
docker compose down
```

데이터 포함 삭제:
```bash
docker compose down -v
```
