package sejong.smartnotice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Device;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.Town;
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
public class UserService {

    @Autowired EntityManager em;
    @Autowired UserRepository userRepository;

    // 회원가입
    public void register(User user) {
        log.info("== 유저 회원가입 ==");

        Device device = Device.builder().build();
        user.setDevice(device);

        userRepository.save(user);
    }
    
    // 유저 조회
    public User findById(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if(opt.isEmpty()) {
            log.warn("회원을 찾지 못했습니다");
            throw new RuntimeException("대충 에러 던지기");
        }
        return opt.get();
    }

    // 유저랑 마을 연결
    public void selectTown(Long townId, User user) {
        Town town = em.find(Town.class, townId);
        user.setTown(town);
        // 유저랑 마을은 단방향 관계로 만들고 필요시 조인해서 사용하기
    }

    // 유저에 의한 긴급호출
    public void makeEmergencyAlert(User user) {
        Device device = user.getDevice();
        device.setEmergency(true); // 단말기에 긴급정보 표시

        EmergencyAlert alert = EmergencyAlert.builder() // 긴급 알림 생성
                .device(device)
                .alertTime(LocalDateTime.now()).build();

        List<Supporter> supporterList = user.getSupporterList();
        for (Supporter supporter : supporterList) {
            // 회원과 연결된 보호자에게 알림
            supporter.emergencyCall(alert);
        }
        em.persist(alert);
    }
}
