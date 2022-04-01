package sejong.smartnotice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.TownRepository;
import sejong.smartnotice.repository.UserRepository;
import sejong.smartnotice.service.DeviceService;
import sejong.smartnotice.service.SupporterService;
import sejong.smartnotice.service.TownService;
import sejong.smartnotice.service.UserService;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
public class Test {

    @Autowired UserService userService;
    @Autowired TownService townService;
    @Autowired DeviceService deviceService;
    @Autowired SupporterService supporterService;
    @Autowired EntityManager em;

    @Rollback(false)
    @org.junit.jupiter.api.Test
    void test() {
        /// 초기화 ///

        townService.registerTown("마을1", 1L);
        townService.registerTown("마을2", 1L);
        townService.registerTown("마을3", 2L);

        em.flush();
        em.clear();

        ////

        Town town = townService.findTownById(1L);
        Device device = deviceService.findDeviceById(1L);
    }
}

