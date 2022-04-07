# 스마트 마을알림 시스템
엄청난 스마트 마을알림 시스템을 만들어보자
<br>

## 1. 기술 스택
### Spring Boot
- JPA
- thymeleaf
- Spring Security
### MySQL
### AWS EC2

<br>

## 2. To-Do List
<4월 1추자> - 회원 관리 서비스 구현하기
1. ~~마을 주민, 관리자, 마을에 대한 기본적인 CRUD~~ (완료)
2. ~~마을 관리자 등록 및 관리 기능 구현~~ (완료)
   - 중복해서 등록 및 출력되는 문제 해결 필요
   - 마을 관리자를 삭제 시 관리자 계정도 삭제할 것인가?
3. ~~마을 주민을 관리자로 등록할 수 있도록 하기~~ (완료)
   - 등록 시 마을 주민 정보를 이용해 관리자 계정을 생성함
4. ~~입력 폼 데이터 검증~~ (완료)
   - 회원가입 시 아이디, 전화번호 중복 검증은 좀 더 알아보기 (AJAX를 써야하는지?)
   - 프론트 단 검증은 나중에 생각하고 서버 단에서 할 수 있는 오브젝트 에러 넘기기
5. ~~보호자와 마을 주민 연결~~ (완료)
6. 검색 기능에 옵션 추가 (마을별, 지역별 검색 등)
   - 동적 쿼리도 공부하기
7. ~~마을 추가 시 지역을 selectbox로 선택할 수 있도록 구현하기~~ (완료)
8. ~~주민 회원가입시 마을 선택할 수 있도록 구현하기~~ (완료)
9. 관제 사이트에 대한 로그인, 로그아웃, 권한 관리 기능 구현

+ ~~AWS 배포해서 팀원이랑 공유하기~~ (완료)
<br>
  
<4월 2주차> - 권한 관리, 방송기능 구현

<br>


+ 프론트 화면 나오는 대로 적용 후 공유하기
+ 테스트 코드 작성을 생활화...