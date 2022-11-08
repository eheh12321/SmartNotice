#!/bin/bash

REPOSITORY=/home/ec2-user/app/smartnotice_build
PROJECT_NAME=smartnotice

# 1. 443 포트를 사용중인 애플리케이션 kill
sudo kill "$(sudo lsof -t -i:443)"

# 2. AWS S3을 통해 가져온 zip 디렉토리 안에서 실행시킬 jar 파일만 꺼내 이동
echo "> Build 파일 복사"
cp ${REPOSITORY}/zip/build/libs/*.jar ${REPOSITORY}/

# 3. 실행 시킬 jar 파일 이름 변수에 저장
JAR_NAME=$(ls -tr ${REPOSITORY}/*.jar | tail -n 1)
echo "> JAR Name: ${JAR_NAME}"

# 4. 실행 권한 추가 (+x, executable (755, rwx-rx-rx))
echo "> ${JAR_NAME} 에 실행 권한 추가"
chmod +x "${JAR_NAME}"

# 5. jar 파일 실행
echo "> ${JAR_NAME} 실행"
nohup sudo java -jar \
        -Dspring.config.location=classpath:/application.properties,/home/ec2-user/app/application-real-db.properties,classpath:/application-real.properties,/home/ec2-user/app/application-api.properties,/home/ec2-user/app/application-https.properties \
        -Dspring.profiles.active=real \
        "${JAR_NAME}" > $REPOSITORY/nohup.out 2>&1 &