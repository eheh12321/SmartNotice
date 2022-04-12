package sejong.smartnotice.controller;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/announces")
public class AnnounceController {

    @GetMapping("/text")
    public String getTextAnnounceForm() {
        log.info("방송 저장 경로: {}",  getDirectory());
        return "announce/textAnnounce";
    }

    @PostMapping("/text")
    public String getTextAnnounce(@RequestParam String text) throws Exception {
        log.info("== 문자 방송 ==");
        log.info("방송 시각: {}", LocalDateTime.now());
        log.info("문자 길이: {}", text.length());
        log.info("==================");
        if(text.length() > 500) { // 제한
            return "redirect:/announces/text";
        }
        synthesizeText(text, UUID.randomUUID().toString(), getDirectory());
        return "redirect:/announces/text";
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
}
