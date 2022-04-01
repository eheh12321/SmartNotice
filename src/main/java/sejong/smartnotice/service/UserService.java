package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TownService townService;
    private final DeviceService deviceService;

    // 신규 주민 등록
    public Long register(String name, String tel, String address, Long townId, Long deviceId) {
        // 1. 주민이 포함될 마을 조회
        Town town = townService.findTownById(townId);

        // 2. 주민이 사용할 단밀기 조회
        Device device = deviceService.findDeviceById(deviceId);

        // 3. 마을주민 생성
        User user = User.createUser(name, tel, address, town, "아이디", "비밀번호");

        userRepository.save(user);
        return user.getId();
    }

    // 마을 주민 조회
    public User findUserById(Long userId) {
        return validateUserId(userId);
    }

    // 주민 삭제
    public void 주민삭제() {}

    // 주민 마을 수정
    public void ModifyUserTown(Long userId, Long townId) {
        Town town = townService.findTownById(townId);
        User user = validateUserId(userId);
        user.changeTown(town);
    }

    // 주민 단말기 수정
    public void ModifyUserDevice(Long userId, Long deviceId) {
        Device device = deviceService.findDeviceById(deviceId);
        User user = validateUserId(userId);
        user.changeDevice(device);
    }

    public void 주민정보수정(String name, String address, String tel) {}
    public void 주민비밀번호수정() {}

    private User validateUserId(Long userId) {
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) {
            log.warn("마들주민이 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }
}
