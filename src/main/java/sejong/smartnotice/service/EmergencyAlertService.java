package sejong.smartnotice.service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Pause;
import com.twilio.twiml.voice.Say;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.AlertType;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.TownData;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.EmergencyAlertRepository;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmergencyAlertService {

    private final TownDataService townDataService;
    private final EmergencyAlertRepository emRepository;
    private final UserService userService;

    @Value("${twilio.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.token}")
    private String AUTH_TOKEN;

    @Value("${twilio.myTel}")
    private String MY_TEL;

    public void makeEmergencyCall(Long userId) {
        // 1. 전화 대상 조회
        User user = null;
        user = userService.findById(userId);

        // 2. Twilio 초기화
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        String from = MY_TEL; // Twilio 가상 번호

        // 3. 주민과 연결된 보호자를 대상으로 모두 전화
        List<Supporter> supporterList = user.getSupporterList();
        for (Supporter supporter : supporterList) {
            log.info("보호자 이름: {}, 번호: {}에게 알림", supporter.getName(), supporter.getTel());
            // 형식에 맞게 전화번호 변경
            String originalTel = supporter.getTel();
            String localTelNum = "+82";
            String to = localTelNum.concat(originalTel.replace("-", "").substring(1));
            log.info("변환된 전화번호: {}", to);

            try {
                Pause pause = new Pause.Builder().length(2).build();
                Say say = new Say.Builder("스마트 마을 알림 시스템 긴급 알림입니다." + user.getName() + "님에게 긴급 상황이 발생했습니다.").voice(Say.Voice.POLLY_SEOYEON).build();
                Say endMessage = new Say.Builder("스마트 마을 알림 시스템 긴급 알림입니다." + user.getName() + "님에게 긴급 상황이 발생했습니다. 문장은 여기까지입니다.").voice(Say.Voice.POLLY_SEOYEON).build();
                VoiceResponse response = new VoiceResponse.Builder().say(say).pause(pause).say(say).pause(pause).say(endMessage).build();

                Call call = Call.creator(new PhoneNumber(to), new PhoneNumber(from), new Twiml(response.toXml())).create();
                log.info("성공!: {}", call.getSid());
            } catch (ApiException e) {
                e.printStackTrace();
                log.error("API 호출에 실패했습니다 (전화번호가 유효하지 않음)");
            }
        }
        log.info("긴급 호출 성공");
    }

    public Long createAlert(User user, AlertType alertType) {
//        try {
//            makeEmergencyCall(user.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("실패했습니다 ㅜㅜ");
//        }
        EmergencyAlert alert = EmergencyAlert.createAlert(user, alertType);
        emRepository.save(alert);

        // Redis Update
        TownData townData = townDataService.findById(user.getTown().getId());
        townData.setAlertCnt(townData.getAlertCnt() + 1);
        townData.setUserAlertCnt(townData.getUserAlertCnt() + 1);
        townDataService.save(townData);

        return alert.getId();
    }

    public void alertConfirm(Long id) {
        EmergencyAlert alert = findById(id);
        alert.alertConfirm();
    }

    @Transactional(readOnly = true)
    public List<EmergencyAlert> findWithUserByUserId(Long userId) {
        log.info("== 주민별 호출 목록 조회(fetch) ==");
        return emRepository.findWithUserByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<EmergencyAlert> findAllWithUserByTown(Long townId) {
        log.info("== 마을별 긴급 호출 목록 조회(fetch) ==");
//        return emRepository.findAllWithUserByTown(townId);
        return emRepository.findAll();
    }

    public EmergencyAlert findById(Long id) {
        Optional<EmergencyAlert> opt = emRepository.findById(id);
        if (opt.isEmpty()) {
            log.warn("호출 ID가 존재하지 않습니다");
            throw new NullPointerException("호출 ID가 존재하지 않습니다.");
        }
        return opt.get();
    }

    public List<EmergencyAlert> findAllWithUser() {
        log.info("== 전체 긴급 호출 목록 조회(fetch) ==");
        return emRepository.findAllWithUser();
    }
}
