package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.SupporterRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SupporterService {

    private final UserService userService;
    private final SupporterRepository supporterRepository;

    // 회원가입
    public Long register(String name, String tel, String loginId, String loginPw, Long userId) {
        log.info("== (서비스) 보호자 회원가입 ==");
        // 1. 보호자 등록
        Supporter supporter = Supporter.createSupporter(name, tel, loginId, loginPw);
        supporterRepository.save(supporter);

        // 2. 보호자와 주민 연결
        connectWithUser(userId, supporter);
        return supporter.getId();
    }

    // 보호자랑 회원 연결
    public Long connectWithUser(Long userId, Supporter supporter) {
        log.info("== (서비스) 보호자와 마을 주민 연결 ==");
        User user = userService.findById(userId);
        // + 뭔가 검증 로직을 거쳐야함
        supporter.connectUser(user);
        return user.getId();
    }

    // 보호자 정보 수정
    public Long modifySupporterInfo(Long id, String name, String tel) {
        log.info("== (서비스) 보호자 정보 수정 ==");
        Supporter supporter = findById(id);
        supporter.modifySupporterInfo(name, tel);
        return supporter.getId();
    }


    // 보호자 조회
    public Supporter findById(Long supporterId) {
        log.info("== (서비스) 보호자 아이디로 조회 ==");
        return validateSupporterId(supporterId);
    }

    // 보호자 목록 조회
    public List<Supporter> findAll() {
        log.info("== (서비스) 보호자 전체 조회 ==");
        return supporterRepository.findAll();
    }

    public Supporter findByLoginId(String loginId) {
        log.info("== (서비스) 보호자 로그인 아이디로 조회 ==");
        return supporterRepository.findByLoginId(loginId);
    }

    public Supporter findByTel(String tel) {
        log.info("== (서비스) 보호자 전화번호로 조회 ==");
        return supporterRepository.findByTel(tel);
    }

    // 보호자 삭제
    public void delete(Long supporterId) {
        log.info("== (서비스) 보호자 삭제 ==");
        Supporter supporter = validateSupporterId(supporterId);
        supporterRepository.delete(supporter);
    }

    private Supporter validateSupporterId(Long supporterId) {
        log.info("== (서비스) 보호자 아이디 검증 ==");
        Optional<Supporter> opt = supporterRepository.findById(supporterId);
        if(opt.isEmpty()) {
            log.warn("보호자가 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }
}
