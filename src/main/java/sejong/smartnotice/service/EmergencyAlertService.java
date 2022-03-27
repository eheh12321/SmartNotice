package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Device;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmergencyAlertService {

    private final UserRepository userRepository;
    private final EntityManager em;

    public void 유저긴급호출() {
        
    }
    
    public void 단말기긴급호출() {

    }

    // 호출 잘 되는지 테스트용 (나중에 삭제)
    public void 호출테스트(Long userId) {
        Optional<User> opt = userRepository.findById(userId);
        if(opt.isEmpty()) {
            log.warn("사용자가 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        User user = opt.get();

        Device device = user.getDevice();
        device.setEmergency(true); // 단말기에 긴급정보 표시

        EmergencyAlert alert = EmergencyAlert.builder() // 긴급 알림 생성
                .device(device)
                .user(user)
                .alertTime(LocalDateTime.now()).build();
        em.persist(alert);

        List<Supporter> supporterList = user.getSupporterList();
        for (Supporter supporter : supporterList) {
            // 회원과 연결된 보호자에게 알림
            supporter.emergencyCall(alert);
        }
    }
}
