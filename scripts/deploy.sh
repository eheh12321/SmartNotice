#!/bin/bash

REPOSITORY=/home/ec2-user/app/smartnotice_build
PROJECT_NAME=smartnotice

sudo kill $(sudo lsof -t -i:443)

echo "> Build 파일 복사"
echo "> cp $REPOSITORY/zip/*.jar $REPOSITORY/"

cp $REPOSITORY/zip/build/libs/*.jar $REPOSITORY/

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행 권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

nohup java -jar \
        -Dspring.config.location=classpath:/application.properties,/home/ec2-user/app/application-real-db.properties,classpath:/application-real.properties,/home/ec2-user/app/application-api.properties,/home/ec2-user/app/application-https.properties \
        -Dspring.profiles.active=real \
        $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &