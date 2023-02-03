#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE="$PROJECT_ROOT/cazait-server.jar"

LOG_DIR="$PROJECT_ROOT/logs"
APP_LOG="$LOG_DIR/application.log"
ERROR_LOG="$LOG_DIR/error.log"
DEPLOY_LOG="$LOG_DIR/deploy.log"

TIME_NOW=$(date +%c)

# build 파일 복사
echo "$TIME_NOW > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE
cp $PROJECT_ROOT/application.yml application.yml
cp $PROJECT_ROOT/application-aws.yml application-aws.yml
cp $PROJECT_ROOT/application-dev.yml application-dev.yml
cp $PROJECT_ROOT/application-kakao.yml application-kakao.yml

# jar 파일 실행
echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
nohup java -jar $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG