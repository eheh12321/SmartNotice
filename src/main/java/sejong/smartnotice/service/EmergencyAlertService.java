package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.EmergencyAlertRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmergencyAlertService {

    private final EmergencyAlertRepository emRepository;

    public Long createAlert(User user) {
        EmergencyAlert alert = EmergencyAlert.createAlert(user);
        emRepository.save(alert);
        return alert.getId();
    }
}
