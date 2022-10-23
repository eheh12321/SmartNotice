package sejong.smartnotice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.config.MqttConfig;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.announce.AnnounceType;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.dto.AnnounceOutputDTO;
import sejong.smartnotice.dto.AnnounceRegisterDTO;
import sejong.smartnotice.dto.MqttAnnounceJson;
import sejong.smartnotice.repository.AnnounceRepository;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnnounceService {

    private final AdminService adminService;
    private final TownService townService;
    private final MqttConfig.MyGateway mqttGateway;
    private final AnnounceRepository announceRepository;

    public Long registerAnnounce(AnnounceRegisterDTO registerDTO) {
        log.info("== 방송 등록 ==");

        Admin admin = adminService.findById(registerDTO.getAdminId());

        // 1. 방송 파일 저장
        String fileName = UUID.randomUUID().toString();
        String path = getDirectory(); // 폴더 생성
        byte[] audioContents;

        // == 문자 방송인 경우 ==
        if(registerDTO.getType().equals(AnnounceType.TEXT)) {
            if(registerDTO.getVoiceData() != null) {
                log.info("기존 파일 이용");
                audioContents = Base64.getDecoder().decode(registerDTO.getVoiceData());
                saveAudioContents(audioContents, fileName, path);
            } else {
                log.info("새로 생성");
                AnnounceOutputDTO outputDTO = makeTextAnnounce(registerDTO.getTextData());
                audioContents = outputDTO.getAudioContents();
                saveAudioContents(audioContents, fileName, path); // 저장
            }
        } else { // == 음성 방송인 경우 ==
            audioContents = Base64.getDecoder().decode(registerDTO.getVoiceData());
            saveAudioContents(audioContents, fileName, path);
        }

//        // JSON 변환 및 MQTT 전송
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            MqttAnnounceJson json = new MqttAnnounceJson(admin.getName(), registerDTO.getTitle(), registerDTO.getTextData(),
//                    Base64.getEncoder().encodeToString(audioContents), registerDTO.getType().toString(), registerDTO.getCategory().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            String jsonInString = mapper.writeValueAsString(json);
//            mqttGateway.sendToMqtt(jsonInString, "announce", 1);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("JSON 변환 실패!!");
//        }

        // 2. 방송 대상 마을 추출
        List<Town> townList = new ArrayList<>();
        for (Long tid : registerDTO.getTownId()) {
            Town town = townService.findById(tid);
            townList.add(town);
        }

        // 3. 방송 생성
        Announce announce = Announce.makeAnnounce(admin.getName(), registerDTO.getTextData(), registerDTO.getCategory(),
                registerDTO.getType(), townList, path, fileName, registerDTO.getTitle());
        announceRepository.save(announce);

        return announce.getId();
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

    public void delete(Long announceId) {
        log.info("== 방송 삭제 ==");
        // 1. 방송 조회
        Announce announce = findById(announceId);

        // 2. 방송 파일 삭제
        File savedFile = new File("." + announce.getFullPath());
        boolean isDelete = savedFile.delete();

        // 3. DB 삭제
        if(isDelete) {
            announceRepository.delete(announce);
        } else {
            log.warn("방송 삭제에 실패했습니다");
            throw new IllegalStateException("방송 삭제에 실패했습니다");
        }
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

    // 문자 -> 음성파일 변환
    private static byte[] synthesizeText(String text) throws Exception {
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

    private static boolean saveAudioContents(byte[] audioContents, String fileName, String directory) {
        String path = directory + File.separator + fileName + ".mp3";
        try (OutputStream out = new FileOutputStream(path)) {
            out.write(audioContents);
            log.info("파일 저장에 성공했습니다: {}", path);
            return true;
        } catch (Exception e) {
            log.warn("파일 저장에 실패하였습니다");
            return false;
        }
    }

    // 날짜에 따른 업로드 파일 경로 생성 (연/월/일)
    private String getDirectory() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String str = "storage" + File.separator
                + sdf.format(date).replace("-", File.separator); // ex) 2022\03\08;

        File file = new File(str);
        if(!file.exists()) {
            file.mkdirs(); // 디렉토리가 존재하지 않는다면 새로 생성
        }
        return str;
    }

}
