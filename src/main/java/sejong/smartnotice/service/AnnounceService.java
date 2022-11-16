package sejong.smartnotice.service;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.helper.dto.AnnounceOutputDTO;
import sejong.smartnotice.helper.dto.AnnounceRegisterDTO;
import sejong.smartnotice.helper.event.CreatedAnnounceEvent;
import sejong.smartnotice.repository.AnnounceRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnnounceService {

    private final AdminService adminService;
    private final TownService townService;
    private final AnnounceRepository announceRepository;

    private final ApplicationEventPublisher publisher;

    public Long registerAnnounce(AnnounceRegisterDTO registerDTO) {
        log.info("== 방송 등록 ==");
        Admin admin = adminService.findById(registerDTO.getAdminId());

        // 1. 방송 생성
        List<Town> townList = registerDTO.getTownId().stream()
                .map(townService::findById)
                .collect(Collectors.toList());

        Announce announce = Announce.makeAnnounce(admin.getName(), registerDTO.getTextData(), registerDTO.getCategory(),
                registerDTO.getType(), townList, registerDTO.getTitle());
        announceRepository.save(announce);

        // 2. 파일 저장을 위한 Event 생성
        log.info(">> 방송 저장 Event 요청");
        byte[] audioContents = registerDTO.getVoiceData() != null ? Base64.getDecoder().decode(registerDTO.getVoiceData()) : null;
        publisher.publishEvent(new CreatedAnnounceEvent(this, announce, audioContents));

        return announce.getId();
    }

    public void delete(Long announceId) {
        log.info("== 방송 삭제 ==");
        Announce announce = findById(announceId);
        announceRepository.delete(announce);
    }

    public Announce findById(Long id) {
        log.info("== 방송 아이디 조회 ==");
        Optional<Announce> opt = announceRepository.findById(id);
        if(opt.isEmpty()) {
            log.warn("방송이 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }

    @Transactional(readOnly = true)
    public List<Announce> findAll() {
        log.info("== 방송 전체 목록 조회 ==");
        return announceRepository.findAllAnnounceWithTown();
    }

    @Transactional(readOnly = true)
    public List<Announce> findAllAnnounceToTownById(Long townId) {
        log.info("== 방송 목록 전체 조회(fetch) ==");
        return announceRepository.findAllAnnounceToTownById(townId);
    }

    @Transactional(readOnly = true)
    public List<Announce> findAllAnnounceToTown(List<Town> townList) {
        log.info("== 특정 마을 대상 방송 목록 조회 ==");
        return announceRepository.findAllAnnounceToTown(townList);
    }

    /**
     * Google TTS가 만료되면서 쓸모없어진 코드들
     */
    // 문자 -> 음성파일 변환
    private byte[] synthesizeText(String text) throws Exception {
        // Instantiates a client
        String jsonPath = "custom/tactile-depot-347005-5be01ad6f1cb.json";
        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(jsonPath)));
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
        log.info(settings.toString());

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(settings)) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Build the voice request
            VoiceSelectionParams voice =
                    VoiceSelectionParams.newBuilder()
                            .setLanguageCode("ko-KR") // languageCode
                            .setSsmlGender(SsmlVoiceGender.FEMALE) // ssmlVoiceGender = SsmlVoiceGender.FEMALE
                            .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig =
                    AudioConfig.newBuilder()
                            .setAudioEncoding(AudioEncoding.MP3) // MP3 audio.
                            .build();

            // Perform the text-to-speech request
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            return response.getAudioContent().toByteArray();
        }
    }

    // 문자방송 파일 생성
    public AnnounceOutputDTO makeTextAnnounce(String text) {
        log.info("== 문자 방송 생성 (API 통신) ==");
        log.info("방송 시각: {}", LocalDateTime.now());
        log.info("방송 내용: {}", text);
        log.info("문자 길이: {}", text.length());
        log.info("==================");
        if(text.length() > 1000) { // 제한
            throw new IllegalStateException("1000자를 초과할 수 없습니다");
        }
        try {
            byte[] audioContents = synthesizeText(text); // API 통신
            log.info("성공!");
            return new AnnounceOutputDTO(audioContents);
        } catch (Exception e) {
            log.warn("방송 파일 생성 실패");
            e.printStackTrace();
            return null;
        }
    }

}
