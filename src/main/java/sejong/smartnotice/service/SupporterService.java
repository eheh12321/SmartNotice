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
import sejong.smartnotice.domain.member.Account;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.helper.dto.SupporterModifyDTO;
import sejong.smartnotice.helper.dto.request.register.SupporterRegisterDTO;
import sejong.smartnotice.repository.SupporterRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SupporterService implements UserDetailsService {

    private final UserService userService;
    private final SupporterRepository supporterRepository;
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    // 회원가입
    public Long register(SupporterRegisterDTO registerDTO) {
        log.info("== 보호자 회원가입 ==");
        // 1. 중복 검증
        validateDuplicateSupporter(registerDTO.getTel(), registerDTO.getLoginId());

        // 2. 비밀번호 암호화
        Account account = Account.createAccount(registerDTO.getLoginId(), registerDTO.getLoginPw(), PASSWORD_ENCODER);

        // 3. 보호자 등록
        Supporter supporter = Supporter.createSupporter(registerDTO.getName(), registerDTO.getTel(), account);
        supporterRepository.save(supporter);

        // 4. 보호자와 주민 연결
        User user = userService.findByTel(registerDTO.getUserTel());
        connectWithUser(user.getId(), supporter);
        return supporter.getId();
    }

    // 보호자랑 회원 연결
    @Transactional(readOnly = true)
    public Long connectWithUser(Long userId, Supporter supporter) {
        log.info("== 보호자와 마을 주민 연결 ==");
        // 1. 주민 찾기 (없으면 NPE)
        User user = userService.findById(userId);
        
        // 2. 보호자랑 양방향 연결
        supporter.connectUser(user);
        return user.getId();
    }

    // 보호자 정보 수정
    public Long modifySupporterInfo(SupporterModifyDTO modifyDTO) {
        log.info("== 보호자 정보 수정 ==");
        // 보호자 찾기
        Supporter supporter = findById(modifyDTO.getId());
        // 보호자 정보 변경
        supporter.modifySupporterInfo(modifyDTO.getName(), modifyDTO.getTel());
        return supporter.getId();
    }

    // 보호자 삭제
    public void delete(Long supporterId) {
        log.info("== 보호자 삭제 ==");
        Supporter supporter = validateSupporterId(supporterId);
        supporterRepository.delete(supporter);
    }

    // 보호자 조회
    public Supporter findById(Long supporterId) {
        log.info("== 보호자 아이디 조회 ==");
        return validateSupporterId(supporterId);
    }

    // 보호자 목록 조회
    @Transactional(readOnly = true)
    public List<Supporter> findAllWithUser() {
        log.info("== 보호자 전체 조회 ==");
        return supporterRepository.findAllWithUser();
    }

    @Transactional(readOnly = true)
    public Supporter findByLoginId(String loginId) {
        log.info("== 보호자 로그인 아이디로 조회 ==");
        return supporterRepository.findByAccountLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public Supporter findByTel(String tel) {
        log.info("== 보호자 전화번호로 조회 ==");
        return supporterRepository.findByTel(tel);
    }

    private Supporter validateSupporterId(Long supporterId) {
        log.info("== 보호자 아이디 검증 ==");
        Optional<Supporter> opt = supporterRepository.findById(supporterId);
        if(opt.isEmpty()) {
            log.warn("보호자가 존재하지 않습니다");
            throw new NullPointerException("보호자가 존재하지 않습니다");
        }
        return opt.get();
    }

    private void validateDuplicateSupporter(String tel, String loginId) {
        log.info("== 보호자 중복 검증 ==");
        if(supporterRepository.existsSupporterByAccountLoginIdOrTel(loginId, tel)) {
            log.warn("이미 존재하는 회원입니다");
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    //// 스프링 시큐리티

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Supporter supporter = findByLoginId(username);
        if(supporter == null) throw new UsernameNotFoundException("등록되지 않은 사용자입니다");
        return supporter;
    }
}
