package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.EmergencyAlertRepository;

import java.util.List;

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

    @Transactional(readOnly = true)
    public List<EmergencyAlert> findAllWithUser() {
        log.info("== 긴급 호출 목록 조회(fetch) ==");
//        return emRepository.findAllWithUser();
        return emRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<EmergencyAlert> findAllWithUserByTown(Long townId) {
        log.info("== 마을별 긴급 호출 목록 조회(fetch) ==");
//        return emRepository.findAllWithUserByTown(townId);
        return emRepository.findAll();
    }
}
