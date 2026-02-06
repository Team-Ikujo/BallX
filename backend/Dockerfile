# ============================================
# Stage 1: Build - 애플리케이션 빌드 (glibc/Ubuntu)
# ============================================
FROM eclipse-temurin:21-jdk-noble AS build

WORKDIR /workspace

# Gradle Wrapper 및 빌드 설정 파일만 먼저 복사 (의존성 캐싱)
COPY gradlew gradlew
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# Gradle Wrapper 실행 권한 부여
RUN chmod +x gradlew

# 의존성만 먼저 다운로드 (소스 변경 시에도 이 레이어는 캐시됨)
# NOTE: src 없이 실행하므로 일부 태스크가 실패할 수 있어 || true 사용
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사 및 빌드
COPY src/ src/
RUN ./gradlew clean build -x test --no-daemon

# Spring Boot가 생성하는 불필요한 plain JAR 제거
RUN rm -f build/libs/*-plain.jar

# ============================================
# Stage 2: Runtime - 경량 런타임 이미지 (Alpine)
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 보안: non-root 사용자로 실행 (Alpine은 addgroup/adduser 사용)
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# 빌드 결과물 복사
COPY --from=build /workspace/build/libs/*.jar app.jar

# 소유권 설정
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

# JAVA_OPTS 환경변수로 JVM 옵션 전달 가능
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:-} -jar app.jar"]
