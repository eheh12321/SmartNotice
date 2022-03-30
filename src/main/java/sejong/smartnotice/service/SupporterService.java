package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.SupporterRepository;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SupporterService {

    private final UserService userService;
    private final SupporterRepository supporterRepository;

    // 회원가입
    public Long register(String name, String tel, String loginId, String loginPw) {
        log.info("== 보호자 회원가입 ==");
        // 검증로직 들어가야함
        Supporter supporter = Supporter.createSupporter(name, tel, loginId, loginPw);
        supporterRepository.save(supporter);
        return supporter.getId();
    }

    // 보호자랑 회원 연결
    public Long connectWithUser(Long userId, Supporter supporter) {
        User user = userService.findUserById(userId);
        // + 뭔가 검증 로직을 거쳐야함
        supporter.connectUser(user);
        return user.getId();
    }

    // 보호자 조회
    public Supporter findSupporterById(Long supporterId) {
        return validateSupporterId(supporterId);
    }

    // 보호자 정보 수정
    public void 보호자정보수정() {}
    
    // 보호자 삭제
    public void remove(Long supporterId) {
        Supporter supporter = validateSupporterId(supporterId);
        supporterRepository.delete(supporter);
    }

    private Supporter validateSupporterId(Long supporterId) {
        Optional<Supporter> opt = supporterRepository.findById(supporterId);
        if(opt.isEmpty()) {
            log.warn("보호자가 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }
}
