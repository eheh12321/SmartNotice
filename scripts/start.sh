#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ec2-user/app/smartnotice
PROJECT_NAME=smartnotice

echo "> Build 파일 복사"
echo "> cp ${REPOSITORY}/zip/*.jar ${REPOSITORY}/"

cp ${REPOSITORY}/zip/build/libs/*.jar ${REPOSITORY}/

echo "> 새 애플리케이션 배포"
JAR_NAME=$(ls -tr ${REPOSITORY}/*.jar | tail -n 1)

echo "> JAR Name: ${JAR_NAME}"

echo "> ${JAR_NAME} 에 실행 권한 추가"

chmod +x ${JAR_NAME}

echo "> ${JAR_NAME} 실행"

IDLE_PROFILE=$(find_idle_profile) # SubShell을 호출해 함수의 결과값을 리턴받는다

echo "> ${JAR_NAME} 을 profile=${IDLE_PROFILE} 로 실행합니다"

nohup java -jar \
        -Dspring.config.location=classpath:/application.properties,$REPOSITORY/application-real-db.properties,classpath:/application-$IDLE_PROFILE.properties,$REPOSITORY/application-api.properties,$REPOSITORY/application-https.properties \
        -Dspring.profiles.active=${IDLE_PROFILE} \
        ${JAR_NAME} > ${REPOSITORY}/nohup.out 2>&1 &