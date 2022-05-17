package sejong.smartnotice.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.service.EmergencyAlertService;
import sejong.smartnotice.service.UserService;

import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/emergency")
@RequiredArgsConstructor
public class EmergencyAlertController {

    private final EmergencyAlertService emService;
    private final UserService userService;

    @Value("${twilio.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.token}")
    private String AUTH_TOKEN;

    @Value("${twilio.myTel}")
    private String MY_TEL;

    @PostMapping("/{userId}")
    public ResponseEntity<String> makeEmergencyCall(@PathVariable Long userId) throws URISyntaxException {
        log.info("== Twilio 호출 ==");

        // 1. 전화 대상 조회
        User user = null;
        try {
            user = userService.findById(userId);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("마을 주민이 존재하지 않습니다");
        }

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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("API 호출에 실패했습니다 (전화번호가 유효하지 않음)");
            }
        }

        // 4. DB 저장
        emService.createAlert(user);
        return ResponseEntity.status(HttpStatus.OK).body("SUCCESS");
    }
}