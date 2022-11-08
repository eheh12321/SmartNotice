#!/usr/bin/env bash

# 쉬고있는 profile 찾기
function find_idle_profile() {
    # 8081 포트로 요청을 보내봤을 때 응답 코드를 변수로 저장
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/profile)

    if [ "${RESPONSE_CODE}" -ge 400 ] || [ "${RESPONSE_CODE}" -eq 000 ]; # 400보다 크거나, 000(TimeOut)이면 -> Error 발생 = 현재 쉬고있는 포트임
    then
      IDLE_PROFILE=real1 # 8081 포트가 쉬는중
    else
      IDLE_PROFILE=real2 # 8082 포트가 쉬는중
    fi

    echo "${IDLE_PROFILE}"
}

# 쉬고 있는 profile의 port 찾기
function find_idle_port() {
  IDLE_PROFILE=$(find_idle_profile) # SubShell 호출

  if [ "${IDLE_PROFILE}" == real1 ]
  then
    echo "8081"
  else
    echo "8082"
  fi
}
