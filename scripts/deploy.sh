#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/app

source $HOME/.bash_profile

echo "> 이전에 실행 중인 애플리케이션 종료"

CURRENT_PID=$(pgrep -fla java | grep BE | awk '{print $1}')

if [ -z "$CURRENT_PID" ]; then
  echo "이전에 실행 중인 애플리케이션이 없습니다."
else
  echo "> 이전 애플리케이션을 종료합니다. PID: $CURRENT_PID"
  kill -9 $CURRENT_PID
  sleep 10
fi

echo "> 현재 실행 중인 애플리케이션 프로세스 확인"

CHECK_PID=$(pgrep -fla java | grep Mogakko | awk '{print $1}')

if [ -z "$CHECK_PID" ]; then
  echo "이전 애플리케이션 종료 완료"
else
  echo "> 이전 애플리케이션 종료 실패. PID: $CHECK_PID"
  exit 1
fi

echo "> 새로운 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*SNAPSHOT.jar | tail -n 1)

echo "> JAR NAME: $JAR_NAME"

echo "> $JAR_NAME 실행 권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

nohup java -jar $JAR_NAME --server.port=8080 >> $REPOSITORY/nohup.out 2>&1 &

echo "> 새로운 애플리케이션 실행 중"

sleep 10

echo "> 새로운 애플리케이션 실행 로그 확인"

tail -f $REPOSITORY/nohup.out
