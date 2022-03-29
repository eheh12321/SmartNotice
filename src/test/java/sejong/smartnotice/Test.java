package sejong.smartnotice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.TownRepository;
import sejong.smartnotice.repository.UserRepository;
import sejong.smartnotice.service.DeviceService;
import sejong.smartnotice.service.TownService;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
public class Test {

    @Autowired UserRepository userRepository;
    @Autowired TownService townService;
    @Autowired DeviceService deviceService;
    @Autowired EntityManager em;

    @Rollback(false)
    @org.junit.jupiter.api.Test
    void test() {
        /// 초기화 ///

        townService.registerTown("마을1", 1L);
        townService.registerTown("마을2", 1L);
        townService.registerTown("마을3", 2L);
        deviceService.registerDevice();
        deviceService.registerDevice();
        deviceService.registerDevice();

        em.flush();
        em.clear();

        ////

        Town town = townService.findTownById(1L);
        Device device = deviceService.findDeviceById(1L);

        User user = User.builder()
                .name("이름")
                .address("주소")
                .tel("전화번호")
                .town(town)
                .device(device)
                .loginId("로그인ID")
                .loginPw("로그인PW").build();

        userRepository.save(user);

        townService.removeTown(2L);
    }
}

