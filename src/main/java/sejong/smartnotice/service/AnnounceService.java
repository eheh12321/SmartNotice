package sejong.smartnotice.service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.announce.AnnounceType;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.dto.AnnounceOutputDTO;
import sejong.smartnotice.dto.AnnounceRegisterDTO;
import sejong.smartnotice.repository.AnnounceRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnnounceService {

    private final AdminService adminService;
    private final TownService townService;
    private final AnnounceRepository announceRepository;

    // 문자 방송
    public Long registerTextAnnounce(AnnounceRegisterDTO announceRegisterDTO) {
        log.info("== 문자 방송 등록 ==");
        // 1. 방송 파일 저장
        AnnounceOutputDTO outputDTO = makeTextAnnounce(announceRegisterDTO.getText());

        // 2. 방송 대상 마을 추출
        List<Town> townList = new ArrayList<>();
        for (Long tid : announceRegisterDTO.getTownId()) {
            Town town = townService.findById(tid);
            townList.add(town);
        }

        // 3. 방송 생성
        Admin admin = adminService.findById(announceRegisterDTO.getAdminId());
        Announce announce = Announce.makeAnnounce(admin.getName(), announceRegisterDTO.getText(), announceRegisterDTO.getCategory(),
                AnnounceType.TEXT, townList, outputDTO.getPath(), outputDTO.getFileName());
        announceRepository.save(announce);

        return announce.getId();
    }
    
    // 방송 파일 생성
    public AnnounceOutputDTO makeTextAnnounce(String text) {
        log.info("== 문자 방송 생성 (API 통신) ==");
        log.info("방송 시각: {}", LocalDateTime.now());
        log.info("방송 내용: {}", text);
        log.info("문자 길이: {}", text.length());
        log.info("==================");
        if(text.length() > 500) { // 제한
            throw new IllegalStateException("500자를 초과할 수 없습니다");
        }
        String fileName = UUID.randomUUID().toString();
        String path = getDirectory();
        try {
            synthesizeText(text, fileName, path);
        } catch (Exception e) {
            log.warn("방송 파일 생성 실패");
            return null;
        }
        return new AnnounceOutputDTO(fileName, path);
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
    public List<Announce> findAllAnnounce() {
        log.info("== 방송 전체 목록 조회 ==");
        return announceRepository.findAll();
    }

    // 문자 -> 음성파일 변환
    private static ByteString synthesizeText(String text, String fileName, String directory) throws Exception {
        // Instantiates a client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
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
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            String fullPath = "storage" + File.separator + directory + File.separator;
            File path = new File(fullPath);
            if(!path.exists()) {
                path.mkdirs(); // 디렉토리가 존재하지 않는다면 새로 생성
            }
            log.info("저장 경로: {}", fullPath);
            log.info("파일명: {}", fileName);
            try (OutputStream out = new FileOutputStream(fullPath + fileName + ".mp3")) {
                out.write(audioContents.toByteArray());
                return audioContents;
            }
        }
    }

    // 날짜에 따른 업로드 파일 경로 생성 (연/월/일)
    private String getDirectory() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String str = sdf.format(date);

        return str.replace("-", File.separator); // ex) 2022\03\08
    }

}
