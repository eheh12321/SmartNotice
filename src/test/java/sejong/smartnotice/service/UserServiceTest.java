package sejong.smartnotice.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.LoginDTO;
import sejong.smartnotice.dto.UserDTO;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired UserService userService;
    @Autowired TownService townService;
    @Autowired DeviceService deviceService;
    @Autowired SupporterService supporterService;

    // 마을, 단말기-센서, 보호자, 마을주민 함께 연관
    @Test
    @Rollback(false)
    void 주민등록() {
        Town town = createTown();
        Device device = createDevice();
        Supporter supporter1 = createSupporter("보호자1");
        Supporter supporter2 = createSupporter("보호자2");
        Supporter supporter3 = createSupporter("보호자3");

        UserDTO userDTO = new UserDTO("이름", "서울시 광진구", "010-1212-1212", 70);
        LoginDTO loginDTO = new LoginDTO("ID", "PW");
        Long uid = userService.register(userDTO, loginDTO, 1L);

        User user = userService.findUserById(uid);
        supporterService.connectWithUser(uid, supporter1);
        supporterService.connectWithUser(uid, supporter2);
        supporterService.connectWithUser(uid, supporter3);

        List<Supporter> supporterList = user.getSupporterList();

        assertThat(supporterList.size()).isEqualTo(3);
        assertThat(supporterList).contains(supporter1, supporter2, supporter3);
    }

    private Town createTown() {
        Long rid = townService.registerRegion(1L, "서울시", "광진구");
        Long tid = townService.registerTown("마을", rid);
        return townService.findTownById(tid);
    }

    private Device createDevice() {
        Long did = deviceService.register();
        return deviceService.findDeviceById(did);
    }

    private Supporter createSupporter(String name) {
        Long sid = supporterService.register(name, "010-0000-0000", "id", "pw");
        return supporterService.findSupporterById(sid);
    }
}