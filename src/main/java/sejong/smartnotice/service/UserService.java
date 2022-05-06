package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.Account;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.UserModifyDTO;
import sejong.smartnotice.dto.UserRegisterDTO;
import sejong.smartnotice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TownService townService;
    private final DeviceService deviceService;

    // 신규 주민 등록
    public Long register(UserRegisterDTO registerDTO) {
        log.info("== 마을 주민 등록 ==");
        // 중복 검증
        validateDuplicateUser(registerDTO.getTel(), registerDTO.getLoginId());

        // 주민이 포함될 마을 정보 조회
        Town town = townService.findById(registerDTO.getTownId());

        // 비밀번호 암호화
        Account account = Account.createAccount(registerDTO.getLoginId(), registerDTO.getLoginPw(), new BCryptPasswordEncoder());

        // 계정 생성 및 저장
       User user = User.createUser(registerDTO.getName(), registerDTO.getTel(),
                registerDTO.getAddress(), registerDTO.getBirth(), town, account);
        userRepository.save(user);

        return user.getId();
    }

    // 주민 삭제
    public void delete(Long id) {
        log.info("== 마을 주민 삭제 ==");
        User user = findById(id);
        userRepository.delete(user);
    }

    // 주민 정보 수정
    public Long modifyUserInfo(UserModifyDTO modifyDTO) {
        log.info("== 마을 주민 정보 수정 ==");
        // 1. 주민 조회
        User user = findById(modifyDTO.getId());

        // 2. 전화번호 중복 검증 (전화번호가 이미 존재하고, 동일 인물이 아닌 경우)
        User findUser = findByTel(modifyDTO.getTel());
        if(findUser != null && !findUser.getId().equals(user.getId())) {
            log.warn("중복된 전화번호가 존재합니다");
            throw new IllegalStateException("중복된 전화번호가 존재합니다");
        }

        // 3. 정보 수정
        user.modifyUserInfo(modifyDTO.getName(), modifyDTO.getTel(), modifyDTO.getAddress(), modifyDTO.getInfo());

        return user.getId();
    }

    // 마을 주민 조회
    public User findById(Long userId) {
        log.info("== 마을 주민 아이디 조회 ==");
        return validateUserId(userId);
    }

    // 마을 주민 목록 조회
    public List<User> findAll() {
        log.info("== 마을 주민 전체 조회 ==");
        return userRepository.findAll();
    }

    // 마을 주민 검색(이름으로)
    public List<User> findByName(String name) {
        log.info("== 마을 주민 이름 조회 ==");
        return userRepository.findByNameContaining(name);
    }

    public User findByLoginId(String loginId) {
        log.info("== 마을 주민 로그인 아이디 조회 ==");
        return userRepository.findByAccountLoginId(loginId);
    }

    public User findByTel(String tel) {
        log.info("== 마을 주민 전화번호 조회 ==");
        return userRepository.findByTel(tel);
    }

    private User validateUserId(Long userId) {
        log.info("== 마을 주민 아이디 검증 ==");
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) {
            log.warn("마들주민이 존재하지 않습니다");
            throw new NullPointerException("마들주민이 존재하지 않습니다");
        }
        return opt.get();
    }

    // 주민 중복 검증
    private void validateDuplicateUser(String tel, String loginId) {
        log.info("== 마을 주민 중복 검증 ==");
        if(userRepository.existsByTelOrAccountLoginId(tel, loginId)) {
            log.warn("이미 존재하는 회원입니다");
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    //// 스프링 시큐리티

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByLoginId(username);
        if(user == null) throw new UsernameNotFoundException("등록되지 않은 사용자닙니다");
        return user;
    }
}
