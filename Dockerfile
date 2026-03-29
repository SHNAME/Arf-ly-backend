FROM eclipse-temurin:17-jre-alpine

# 컨테이너 내 작업 디렉토리 설정
WORKDIR /app

ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

#  빌드된 JAR 파일을 컨테이너 내부로 복사
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]