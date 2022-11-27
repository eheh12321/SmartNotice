package sejong.smartnotice.service;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.TownAnnounce;
import sejong.smartnotice.domain.TownData;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.announce.AnnounceCategory;
import sejong.smartnotice.domain.announce.AnnounceStatus;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.helper.dto.request.AnnounceRequest;
import sejong.smartnotice.helper.dto.response.AnnounceResponse;
import sejong.smartnotice.helper.event.CreatedAnnounceEvent;
import sejong.smartnotice.repository.AnnounceRepository;

import javax.persistence.EntityNotFoundException;
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
    private final TownDataService townDataService;

    private final ApplicationEventPublisher publisher;

    public AnnounceResponse registerAnnounce(AnnounceRequest registerDTO) {
        log.info("== 방송 등록 ==");
        Admin admin = adminService.findById(registerDTO.getAdminId());

        // 1. 방송 생성
        List<Town> townList = townService.findTownsByTownIdList(registerDTO.getTownId());
        Announce announce = registerDTO.toEntity(admin.getName());

        // TODO: batch insert를 적용해보고 싶은데 나중에 한번 찾아보기
        townList.forEach(town -> {
            TownAnnounce townAnnounce = TownAnnounce.builder()
                    .announce(announce)
                    .town(town).build();
            townAnnounce.createAnnounce();
            townDataService.action(townData -> {
                townData.setAnnounceCnt(townData.getAnnounceCnt() + 1);
                return townData;
            }, town.getId());
        });
        Announce savedAnnounce = announceRepository.save(announce);

        // 2. 파일 저장을 위한 Event 생성
        log.info(">> 방송 저장 Event 요청");
        byte[] audioContents = registerDTO.getVoiceData() != null ? Base64.getDecoder().decode(registerDTO.getVoiceData()) : null;
        publisher.publishEvent(new CreatedAnnounceEvent(this, savedAnnounce, audioContents));

        return AnnounceResponse.from(savedAnnounce);
    }

    public void delete(Long announceId) {
        log.info("== 방송 삭제 ==");
        Announce announce = findById(announceId);
        announceRepository.delete(announce);
    }

    public void setAnnounceStatus(Long id, String directory, String fileName, AnnounceStatus status) {
        Announce announce = findById(id);
        announce.setAnnounceFileSaved(directory, fileName, status);
    }

    // 현재 관리하고 있는 마을과 관련된 방송을 가져오는 메서드
    public List<Announce> findManagedTownAnnounces(Admin admin, String category) {
        List<Announce> announceList;
        
        if (!admin.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            // 마을 관리자인 경우 관리 마을 대상으로 한 방송 목록만 조회
            List<Town> managedTownList = townService.findTownByAdmin(admin);
            announceList = findAllAnnounceToTown(managedTownList);
        } else {
            // 최고 관리자의 경우 전부 다 가져옴
            announceList = findAll();
        }
        // 카테고리 필터링
        switch (category) {
            case "normal":
                announceList = announceList.stream()
                        .filter(announce -> announce.getCategory().equals(AnnounceCategory.NORMAL))
                        .collect(Collectors.toList());
                break;
            case "disaster":
                announceList = announceList.stream()
                        .filter(announce -> announce.getCategory().equals(AnnounceCategory.DISASTER))
                        .collect(Collectors.toList());
                break;
        }
        return announceList;
    }

    public Announce findById(Long id) {
        return announceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("방송이 존재하지 않습니다"));
    }

    @Transactional(readOnly = true)
    public List<Announce> findAll() {
        log.info("== 방송 전체 목록 조회 ==");
        return announceRepository.findAllAnnounceWithTown();
    }

    @Transactional(readOnly = true)
    public List<Announce> findAllAnnounceToTown(List<Town> townList) {
        log.info("== 특정 마을 대상 방송 목록 조회 ==");
        return announceRepository.findAnnouncesByTownList(townList);
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
    public byte[] makeTextAnnounce(String text) {
        log.info("== 문자 방송 생성 (API 통신) ==");
        log.info("방송 시각: {}", LocalDateTime.now());
        log.info("방송 내용: {}", text);
        log.info("문자 길이: {}", text.length());
        log.info("==================");
        if (text.length() > 1000) { // 제한
            throw new IllegalStateException("1000자를 초과할 수 없습니다");
        }
        try {
            byte[] audioContents = synthesizeText(text); // API 통신
            log.info("성공!");
            return audioContents;
        } catch (Exception e) {
            log.warn("방송 파일 생성 실패");
            e.printStackTrace();
            return null;
        }
    }

}
