package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.UserRepository;

import java.util.List;
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
    public Long register(String name, String tel, String address, int age, Long townId, String id, String pw) {
        log.info("== (서비스) 마을 주민 등록 ==");
        // 1. 주민이 포함될 마을 조회
        Town town = townService.findById(townId);

        // 2. 마을주민 생성
        User user = User.createUser(name, tel, address, age, town, id, pw);

        userRepository.save(user);
        return user.getId();
    }

    // 주민 삭제
    public void delete(Long id) {
        log.info("== (서비스) 마을 주민 삭제 ==");
        User user = findById(id);
        userRepository.delete(user);
    }

    // 주민 정보 수정
    public void modifyUserInfo(Long userId, String name, String tel, String address, String info, int age) {
        log.info("== (서비스) 마을 주민 정보 수정 ==");
        User user = findById(userId);
        user.modifyUserInfo(name, tel, address, info, age);
    }

    // 주민 마을 수정
    public void modifyTown(Long userId, Long townId) {
        Town town = townService.findById(townId);
        User user = validateUserId(userId);
        user.modifyTown(town);
    }

    // 주민 단말기 수정
    public void modifyDevice(Long userId, Long deviceId) {
        Device device = deviceService.findDeviceById(deviceId);
        User user = validateUserId(userId);
        user.modifyDevice(device);
    }

    // 마을 주민 조회
    public User findById(Long userId) {
        log.info("== (서비스) 마을 주민 아이디 조회 ==");
        return validateUserId(userId);
    }

    // 마을 주민 목록 조회
    public List<User> findAll() {
        log.info("== (서비스) 마을 주민 전체 조회 ==");
        return userRepository.findAll();
    }

    // 마을 주민 검색(이름으로)
    public List<User> findByName(String name) {
        log.info("== (서비스) 마을 주민 이름 조회 ==");
        return userRepository.findByNameContaining(name);
    }

    public User findByLoginId(String loginId) {
        log.info("== (서비스) 마을 주민 로그인 아이디 조회 ==");
        return userRepository.findByLoginId(loginId);
    }

    public User findByTel(String tel) {
        log.info("== (서비스) 마을 주민 전화번호 조회 ==");
        return userRepository.findByTel(tel);
    }

    private User validateUserId(Long userId) {
        log.info("== (서비스) 마을 주민 검증 ==");
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) {
            log.warn("마들주민이 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }
}
