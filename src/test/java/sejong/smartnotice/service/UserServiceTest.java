package sejong.smartnotice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Device;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.UserRepository;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired EntityManager em;
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;

    @Test
    @Rollback(false)
    void 긴급알림() {
        User u = User.builder()
                .name("이름")
                .address("주소")
                .tel("전화번호").build();
        em.persist(u);

        Device d = Device.builder()
                .sensor1(0)
                .sensor2(0)
                .error(false)
                .emergency(false).build();
        em.persist(d);
        u.setDevice(d); // 유저랑 단말기 연결

        em.flush();
        em.clear();
        System.out.println("============================================");

        userService.makeEmergencyAlert(1L);
    }
}