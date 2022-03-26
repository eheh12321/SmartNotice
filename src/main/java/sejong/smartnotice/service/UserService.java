package sejong.smartnotice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sejong.smartnotice.domain.Device;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired EntityManager em;
    @Autowired UserRepository userRepository;

    public void makeEmergencyAlert(User user) {
        Device device = user.getDevice();
        device.setEmergency(true); // 단말기에 긴급정보 표시

        EmergencyAlert alert = EmergencyAlert.builder() // 긴급 알림 생성
                .device(device)
                .alertTime(LocalDateTime.now()).build();

        List<Supporter> supporterList = user.getSupporterList();
        for (Supporter supporter : supporterList) {
            // 회원과 연결된 보호자에게 알림
        }
        em.persist(alert);
    }
}
