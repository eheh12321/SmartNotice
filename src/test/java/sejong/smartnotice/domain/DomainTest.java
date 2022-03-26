package sejong.smartnotice.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

@SpringBootTest
@Transactional
class DomainTest {

    @Autowired UserRepository userRepository;
    @Autowired EntityManager em;

    @Test
    @Rollback(false)
    void 테스트() {

        for(int i = 1; i <= 5; i++) {
            // 1. 회원 5명 추가
            User user = User.builder()
                    .loginId("아이디" + i)
                    .loginPw("비밀번호" + i)
                    .name("이름" + i)
                    .address("주소" + i)
                    .tel("전화번호" + i).build();

            em.persist(user);

            // 2. 보호자 추가 (회원은 보호자를 여러명 가질수도 있음)
            Supporter supporter = Supporter.builder()
                    .loginId("아이디" + i)
                    .loginPw("비밀번호" + i)
                    .name("보호자" + i)
                    .tel("전화번호" + i)
                    .user(user).build(); // 2-1. 보호자와 회원 연결
            em.persist(supporter);

            // 3. 단말기 추가
            Device device = Device.builder()
                    .sensor1(i)
                    .sensor2(i)
                    .error(false)
                    .emergency(false).build();
            em.persist(device);
            
            user.setDevice(device); // 3-1. 유저와 단말기 연결

            // 4. 마을 추가
            Town town = Town.builder()
                    .name("마을" + i).build();
            em.persist(town);

            user.setTown(town); // 4-1. 유저와 마을 연결

            // 5. 관리자 추가
            Admin admin = Admin.builder()
                    .loginId("아이디" + i)
                    .loginPw("비밀번호" + i)
                    .name("관리자" + i)
                    .tel("전화번호" + i)
                    .type("이장").build();
            em.persist(admin);

            // 7. 긴급호출
            EmergencyAlert alert = EmergencyAlert.builder()
                    .device(device)
                    .alertTime(LocalDateTime.now()).build();
            em.persist(alert);
        }
    }
}
