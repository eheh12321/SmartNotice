# 스마트 마을알림 시스템
엄청난 스마트 마을알림 시스템을 만들어보자!!
<br><br>
-> :confetti_ball: :sparkles: 창의설계 경진대회 (교내 캡스톤) "은상" 수상 :sparkles: :confetti_ball:

## 1. 기술 스택
### Spring Boot
- JPA
- thymeleaf
- Spring Security
- (테스트 프레임워크) Mockito + JUnit5
### MySQL
### AWS EC2 
- 가능하면 RDB도 연결해볼것! :clock130:
### 외부 API
- Google Cloud TTS (기간 만료로 현재 사용불가) :x:
- Twilio (인터넷 전화 API)
- MQTT
### CI/CD 
- Travis CI 연동 완료
- AWS S3, Codedeploy 연동 완료
- Nginx를 이용한 무중단 배포 적용 완료 (8081, 8082 포트 사용)


### 기타
- 도메인 연결 o (현재 안됨..) :x:
- HTTPS 적용 (인증서 기간 만료로 현재 사용불가) :x:
<br>

## 2. 프로젝트 상세
"마을에 공지사항, 재난 상황, 긴급 호출 등의 다양한 알림을 제공하는 시스템"
<details>
   <summary> 시스템 구조도 </summary>
   
![system](https://user-images.githubusercontent.com/74748851/187402314-266fbd26-d64b-4eb0-8af4-85c634a81212.PNG)
</details>

<details>
   <summary> ERD 다이어그램 </summary>
   
![erd](https://user-images.githubusercontent.com/74748851/187402401-a7ead2b8-aab6-4492-9b05-9887e700e742.PNG)
</details>
<br>

- 도메인: <https://smarttownnotice.gq> (현재 접속불가 :x:)
<br>

## 3. To-Do List
1. 테스트용 실험실 만들기 (무작위 마을, 유저 등록같은 테스트 기능 만들기)
2. 마을 주민 간 소통 게시판 만들기 (CRUD 다시 리마인드 하기)
    - 테스트 코드 꼼꼼하게 작성하면서 만들어보기
3. OAuth 이용한 소셜 로그인 기능 추가
4. 자동 로그인 / 로그인 유지 기능 만들어보기
5. (시간 나면) 디자인도 조금.. 이쁘게 다듬기
    - flexbox 이용해서 동적으로 디자인하기
<br>
   
## 4. 구현된 기능
- 관리자 회원가입, 수정, 조회, 삭제 <br>
- 마을 주민 회원가입, 수정, 조회, 삭제 <br>
- 보호자 회원가입, 수정, 조회, 삭제 <br>
- 마을 등록, 수정, 조회, 삭제, 마을 관리자 등록 <br>
- 로그인, 로그아웃 <br>
- 문자 방송, 음성 방송(녹음을 통해 / 첨부파일을 통해) + 방송 저장 전 미리듣기 <br>
- 방송 조회, 삭제, 다시 듣기 <br>
- 긴급 호출 전화 <br>
- 단말기와 MQTT로 연동

<br>
<hr/>

+ 테스트 코드 작성을 생활화...
+ **DTO는 서비스 계층에서 변환하기**
+ **절대 개인정보가 담긴 파일을 Github에 올리지 말것!!!!!**
