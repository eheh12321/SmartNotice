package sejong.smartnotice.service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.announce.AnnounceCategory;
import sejong.smartnotice.domain.announce.AnnounceType;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.dto.AnnounceDTO;
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

    public Long makeTextAnnounce(AnnounceDTO announceDTO) throws Exception {
        log.info("== 문자 방송 ==");
        log.info("방송 시각: {}", LocalDateTime.now());
        log.info("방송 내용: {}", announceDTO.getText());
        log.info("문자 길이: {}", announceDTO.getText().length());
        log.info("==================");
        if(announceDTO.getText().length() > 500) { // 제한
            throw new IllegalStateException("500자를 초과할 수 없습니다");
        }

        ByteString AudioFile = synthesizeText(announceDTO.getText(), UUID.randomUUID().toString(), getDirectory());

        // 1. 방송 대상 마을 추출
        List<Town> townList = new ArrayList<>();
        for (Long tid : announceDTO.getTownId()) {
            Town town = townService.findById(tid);
            townList.add(town);
        }

        // 2. 방송 생성
        Admin admin = adminService.findById(announceDTO.getAdminId());
        Announce announce = Announce.makeAnnounce(admin, announceDTO.getText(), AnnounceCategory.NORMAL, AnnounceType.TEXT, townList, getDirectory());
        announceRepository.save(announce);

        return announce.getId();
    }


    // 문자 -> 음성파일 변환
    public static ByteString synthesizeText(String text, String fileName, String directory) throws Exception {
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
            try (OutputStream out = new FileOutputStream(directory + "\\" + fileName + ".mp3")) {
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

        String directory =  "storage" + "\\" + str.replace("-", File.separator); // ex) 2022\03\08

        File uploadPath = new File(directory);
        if(!uploadPath.exists()) {
            uploadPath.mkdirs(); // 디렉토리가 존재하지 않는다면 새로 생성
        }
        return directory;
    }

    public Announce findAnnounceById(Long id) {
        Optional<Announce> opt = announceRepository.findById(id);
        if(opt.isEmpty()) {
            log.warn("방송이 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }

    public List<Announce> findAllAnnounce() {
        return announceRepository.findAll();
    }
}
