package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.LoginDTO;
import sejong.smartnotice.dto.UserDTO;
import sejong.smartnotice.repository.UserRepository;

import java.util.ArrayList;
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
    public Long register(UserDTO userDTO, LoginDTO loginDTO, Long townId) {
        // 1. 주민이 포함될 마을 조회
        Town town = townService.findTownById(townId);

        // 2. 마을주민 생성
        User user = User.createUser(userDTO.getName(), userDTO.getTel(), userDTO.getAddress(), userDTO.getAge(), town,
                loginDTO.getLoginId(), loginDTO.getLoginPw());

        userRepository.save(user);
        return user.getId();
    }

    // 마을 주민 조회
    public User findUserById(Long userId) {
        return validateUserId(userId);
    }

    // 마을 주민 목록 조회
    public List<User> getUserList() {
        return userRepository.findAll();
    }
    
    // 주민 삭제
    public void remove(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    // 주민 정보 수정
    public void changeUserInfo(Long userId, UserDTO userDTO) {
        User user = findUserById(userId);
        user.changeMemberInfo(userDTO.getName(), userDTO.getTel());
        user.changeUserInfo(userDTO.getAddress(), userDTO.getInfo(), userDTO.getAge());
    }

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

    // 마을 주민 검색(이름으로)
    public List<User> getUserListByName(String name) {
        return userRepository.findByNameContaining(name);
    }

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
