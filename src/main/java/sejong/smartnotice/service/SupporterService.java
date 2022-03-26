package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.SupporterRepository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SupporterService {

    private final SupporterRepository supporterRepository;
    private final EntityManager em;

    // 회원가입
    public void register(Supporter supporter) {
        log.info("== 보호자 회원가입 ==");
        supporterRepository.save(supporter);
    }

    // 보호자랑 회원 연결
    public void selectUser(Long userId, Supporter supporter) {
        User user = em.find(User.class, userId);
        supporter.setUser(user);
        user.getSupporterList().add(supporter); // 나중에 편의 메소드로 합칠것!
    }

    // 보호자 조회
    public Supporter findById(Long id) {
        Optional<Supporter> opt = supporterRepository.findById(id);
        if(opt.isEmpty()) {
            log.warn("회원을 찾지 못했습니다");
            throw new RuntimeException("대충 에러 던지기");
        }
        return opt.get();
    }
}
