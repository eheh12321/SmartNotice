use capstone;

-- 관리자 초기 세팅
insert into admin(login_id, login_pw, name, tel, type) values("id", "pw", "최고관리자", "010-1234-1234", "ROOT");
insert into admin(login_id, login_pw, name, tel, type) values("id", "pw", "관리자1", "010-1234-1234", "ADMIN");
insert into admin(login_id, login_pw, name, tel, type) values("id", "pw", "관리자2", "010-1234-1234", "ADMIN");
insert into admin(login_id, login_pw, name, tel, type) values("id", "pw", "관리자3", "010-1234-1234", "ADMIN");
insert into admin(login_id, login_pw, name, tel, type) values("id", "pw", "관리자4", "010-1234-1234", "ADMIN");
insert into admin(login_id, login_pw, name, tel, type) values("id", "pw", "관리자5", "010-1234-1234", "ADMIN");

-- 마을 초기 세팅
insert into town(name) values("마을1");
insert into town(name) values("마을2");
insert into town(name) values("마을3");
insert into town(name) values("마을4");
insert into town(name) values("마을5");

-- 단말기 초기 세팅(사용자 수만큼만)
insert into device (emergency, error, sensor1, sensor2) values (false, false, 0, 0);
insert into device (emergency, error, sensor1, sensor2) values (false, false, 0, 0);
insert into device (emergency, error, sensor1, sensor2) values (false, false, 0, 0);
insert into device (emergency, error, sensor1, sensor2) values (false, false, 0, 0);
insert into device (emergency, error, sensor1, sensor2) values (false, false, 0, 0);
insert into device (emergency, error, sensor1, sensor2) values (false, false, 0, 0);

-- 사용자 초기 세팅
insert into user(address, device_id, name, tel, town_id) values("주소1", 1, "회원1", "010-1111-1111", 1);
insert into user(address, device_id, name, tel, town_id) values("주소2", 2, "회원2", "010-1111-1111", 1);
insert into user(address, device_id, name, tel, town_id) values("주소3", 3, "회원3", "010-1111-1111", 1);
insert into user(address, device_id, name, tel, town_id) values("주소4", 4, "회원4", "010-1111-1111", 2);
insert into user(address, device_id, name, tel, town_id) values("주소5", 5, "회원5", "010-1111-1111", 3);
insert into user(address, device_id, name, tel, town_id) values("주소6", 6, "회원6", "010-1111-1111", 4);

-- 보호자 초기 세팅
insert into supporter (login_id, login_pw, name, tel, user_id) values("id", "pw", "보호자1", "010-2222-2222", 1);
insert into supporter (login_id, login_pw, name, tel, user_id) values("id", "pw", "보호자2", "010-2222-2222", 1);
insert into supporter (login_id, login_pw, name, tel, user_id) values("id", "pw", "보호자3", "010-2222-2222", 2);
insert into supporter (login_id, login_pw, name, tel, user_id) values("id", "pw", "보호자4", "010-2222-2222", 3);
insert into supporter (login_id, login_pw, name, tel, user_id) values("id", "pw", "보호자5", "010-2222-2222", 4);
insert into supporter (login_id, login_pw, name, tel, user_id) values("id", "pw", "보호자6", "010-2222-2222", 4);

-- 관리자랑 마을 연결
insert into admin_town (admin_id, town_id) values (1, 1);
insert into admin_town (admin_id, town_id) values (1, 2);
insert into admin_town (admin_id, town_id) values (1, 3);
insert into admin_town (admin_id, town_id) values (1, 4);
insert into admin_town (admin_id, town_id) values (1, 5);
insert into admin_town (admin_id, town_id) values (2, 2);
insert into admin_town (admin_id, town_id) values (3, 3);
insert into admin_town (admin_id, town_id) values (4, 4);
insert into admin_town (admin_id, town_id) values (5, 5);

select * from device;
select * from user;
select * from admin;
select * from town;
select * from admin_town;