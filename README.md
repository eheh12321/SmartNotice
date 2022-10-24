# 스마트 마을알림 시스템
스마트 마을 알림 시스템은 마을들에 대한 종합적인 관제 서비스를 제공하면서 각 마을에 문자/음성 방송 송신, 사용자 긴급 호출 수신 등 시골 마을 또는 독거 노인 관리 서비스를 제공하는 시스템입니다. 

<br>

<img alt="main" src="https://user-images.githubusercontent.com/74748851/197440390-dcf08b57-79af-4696-9f47-e51ec3c181d2.png" height="400"/>

* 배포 링크: https://www.smarttownnotice.gq/
  - 관리자 계정: testSuperAdmin / testSuperAdmin!  
  - 주민 계정: testUser / testUser!
  - 보호자 계정: testSupporter / testSupporter!
<br>

## 1. 기술 스택
### Back-End
- Java 11
- Spring framework 2.7.x
- JPA
- Spring Security
### DB
- MySQL
### Server
- AWS EC2
### CI/CD 
- Jenkins
- AWS CodeDeploy

<br>

## 2. 프로젝트 상세
### 2-1. 시스템 구조도
![image](https://user-images.githubusercontent.com/74748851/197469874-4fd70d0d-0d93-437e-b387-7227b3545b8b.png)

### 2-2. ERD 다이어그램
![erd](https://user-images.githubusercontent.com/74748851/187402401-a7ead2b8-aab6-4492-9b05-9887e700e742.PNG)

### 2-3. 프로젝트 회고
- https://www.notion.so/dcca07ba3275410e95afe00b7a4953f1
<br>
   
## 3. 구현된 기능
- 관리자 회원가입, 수정, 조회, 삭제 <br>
- 마을 주민 회원가입, 수정, 조회, 삭제 <br>
- 보호자 회원가입, 수정, 조회, 삭제 <br>
- 마을 등록, 수정, 조회, 삭제, 마을 관리자 등록 <br>
- 로그인, 로그아웃 <br>
- 문자 방송, 음성 방송(녹음을 통해 / 첨부파일을 통해) + 방송 저장 전 미리듣기 <br>
- 방송 조회, 삭제, 다시 듣기 <br>
- 긴급 호출 전화 <br>
- 단말기와 MQTT로 연동 <br>
- 기상청 오픈 API를 이용해 날씨 정보 조회 
