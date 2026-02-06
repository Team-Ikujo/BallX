# ============================================================
# GoTi Development Makefile
# Docker를 몰라도 make 명령어로 개발환경을 실행할 수 있습니다
# 사용법: make help
# ============================================================

.DEFAULT_GOAL := help

# ── 변수 ──────────────────────────────────────────────────
COMPOSE_BE := docker compose -f backend/docker-compose.yml
COMPOSE_MON := docker compose -f monitoring/docker-compose.yml

# ── Help ──────────────────────────────────────────────────
.PHONY: help
help: ## 사용 가능한 명령어 목록
	@awk 'BEGIN {FS = ":.*##"; printf "\n\033[1m  GoTi Makefile\033[0m\n  사용법: make \033[36m<target>\033[0m\n"} \
		/^[a-zA-Z_-]+:.*?##/ { printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2 } \
		/^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) }' $(MAKEFILE_LIST)
	@echo ""

##@ 처음 시작하기

.PHONY: setup
setup: ## .env 파일 생성 + 안내 (최초 1회)
	@if [ -f backend/.env ]; then \
		echo "  backend/.env 이미 존재합니다."; \
	else \
		cp backend/.env.example backend/.env; \
		echo ""; \
		echo "  backend/.env 파일이 생성되었습니다!"; \
		echo ""; \
		echo "  다음 단계:"; \
		echo "    1. backend/.env 열어서 빈 값 채우기"; \
		echo "       - JWT_SECRET_KEY (필수): openssl rand -base64 64"; \
		echo "       - OAuth 키들은 선택사항"; \
		echo "    2. make up 으로 실행"; \
	fi
	@echo ""

##@ 개발환경

.PHONY: up down restart logs logs-be ps status
up: check-env check-secrets ## 전체 실행 (Postgres + Redis + Backend)
	@$(COMPOSE_BE) up -d
	@echo ""
	@echo "  Backend:  http://localhost:8080"
	@echo "  Postgres: localhost:5432"
	@echo "  Redis:    localhost:6379"
	@echo ""
	@echo "  로그 보기: make logs"
	@echo ""

down: ## 전체 중지
	@$(COMPOSE_BE) down

restart: ## 전체 재시작
	@$(COMPOSE_BE) restart

logs: ## 전체 로그 (실시간, Ctrl+C로 종료)
	@$(COMPOSE_BE) logs -f

logs-be: ## 백엔드 로그만
	@$(COMPOSE_BE) logs -f backend

ps: ## 실행중인 컨테이너 목록
	@docker ps --filter "name=BallX-" --filter "name=ballx-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

status: ## 전체 서비스 상태
	@echo ""
	@echo "  === 백엔드 ==="
	@$(COMPOSE_BE) ps 2>/dev/null || echo "  (없음)"
	@echo ""
	@echo "  === 모니터링 ==="
	@$(COMPOSE_MON) ps 2>/dev/null || echo "  (없음)"
	@echo ""

##@ 개별 서비스

.PHONY: db db-down backend-only build
db: check-env ## DB + Redis만 실행 (IDE에서 백엔드 직접 실행할 때)
	@$(COMPOSE_BE) up -d postgres redis
	@echo ""
	@echo "  Postgres: localhost:5432"
	@echo "  Redis:    localhost:6379"
	@echo ""
	@echo "  IDE에서 백엔드를 직접 실행하세요 (Spring Boot Run)"
	@echo ""

db-down: ## DB + Redis 중지
	@$(COMPOSE_BE) stop postgres redis
	@$(COMPOSE_BE) rm -f postgres redis

backend-only: check-env check-secrets ## 백엔드만 재빌드 + 실행
	@$(COMPOSE_BE) up -d --build backend

build: ## 백엔드 Docker 이미지 빌드
	@$(COMPOSE_BE) build backend

##@ 모니터링 (선택사항)

.PHONY: mon mon-down mon-logs otel
mon: ## 모니터링 스택 실행 (Grafana + Loki + Tempo + OTel)
	@$(COMPOSE_MON) up -d
	@echo ""
	@echo "  Grafana: http://localhost:9000 (admin/admin)"
	@echo "  Loki:    http://localhost:3100"
	@echo "  Tempo:   http://localhost:3200"
	@echo "  OTel:    localhost:4317 (gRPC) / :4318 (HTTP)"
	@echo ""

mon-down: ## 모니터링 스택 중지
	@$(COMPOSE_MON) down

mon-logs: ## 모니터링 로그
	@$(COMPOSE_MON) logs -f

otel: check-env check-secrets db ## OTel Agent와 함께 백엔드 실행 (IDE 없이)
	@echo "  모니터링 스택 + OTel 백엔드 시작..."
	@$(COMPOSE_MON) up -d
	@bash monitoring/run-backend-with-otel.sh

##@ 빌드 / 테스트

.PHONY: test test-build
test: ## Gradle 테스트 실행
	@cd backend && ./gradlew test

test-build: ## 빌드 + 테스트 (PR 전 확인용)
	@cd backend && ./gradlew clean build

##@ Git 워크플로우

.PHONY: pull sync branch branches switch diff
pull: ## 현재 브랜치 최신화 (fetch + pull)
	@git fetch --all --prune
	@git pull --rebase || git pull
	@echo ""
	@echo "  현재 브랜치: $$(git branch --show-current)"
	@echo ""

sync: ## main 기준으로 현재 브랜치 동기화 (rebase)
	@echo "  main 브랜치를 가져옵니다..."
	@git fetch origin main
	@echo "  현재 브랜치에 main을 rebase 합니다..."
	@git rebase origin/main
	@echo ""
	@echo "  완료! 충돌이 있으면 해결 후 git rebase --continue"
	@echo ""

branches: ## 브랜치 목록 (로컬 + 리모트)
	@echo ""
	@echo "  === 로컬 브랜치 (* = 현재) ==="
	@git branch
	@echo ""
	@echo "  === 리모트 브랜치 ==="
	@git branch -r
	@echo ""

switch: ## 브랜치 전환 (make switch b=브랜치명)
	@if [ -z "$(b)" ]; then \
		echo ""; \
		echo "  사용법: make switch b=브랜치명"; \
		echo ""; \
		echo "  예시:"; \
		echo "    make switch b=backend/develop"; \
		echo "    make switch b=main"; \
		echo ""; \
		echo "  사용 가능한 브랜치:"; \
		git branch -a --format='    %(refname:short)' | head -20; \
		echo ""; \
	else \
		git checkout $(b) || git checkout -t origin/$(b); \
		echo ""; \
		echo "  현재 브랜치: $$(git branch --show-current)"; \
		echo ""; \
	fi

diff: ## 변경사항 요약
	@echo ""
	@echo "  === 현재 브랜치: $$(git branch --show-current) ==="
	@echo ""
	@git status -s
	@echo ""

##@ 정리

.PHONY: clean nuke
clean: ## 컨테이너 중지 + 이미지 삭제 (DB 데이터 유지)
	@$(COMPOSE_BE) down --rmi local
	@$(COMPOSE_MON) down --rmi local 2>/dev/null || true

nuke: ## 전부 삭제 (DB 데이터 포함 - 주의!)
	@echo "  DB 데이터가 모두 삭제됩니다!"
	@read -p "  계속? [y/N] " confirm; \
	if [ "$$confirm" = "y" ] || [ "$$confirm" = "Y" ]; then \
		$(COMPOSE_BE) down -v --rmi local --remove-orphans; \
		$(COMPOSE_MON) down -v --rmi local --remove-orphans 2>/dev/null || true; \
		echo "  정리 완료."; \
	else \
		echo "  취소됨."; \
	fi

# ── 내부 타겟 ────────────────────────────────────────────
.PHONY: check-env check-secrets
check-env:
	@if [ ! -f backend/.env ]; then \
		echo ""; \
		echo "  .env 파일이 없습니다!"; \
		echo "  먼저 make setup 을 실행하세요."; \
		echo ""; \
		exit 1; \
	fi

check-secrets:
	@if grep -q "your-jwt-secret-key-base64-encoded" backend/.env 2>/dev/null; then \
		echo ""; \
		echo "  JWT_SECRET_KEY가 아직 기본값입니다!"; \
		echo "  backend/.env 에서 값을 변경하세요."; \
		echo "  생성: openssl rand -base64 64"; \
		echo ""; \
		exit 1; \
	fi
