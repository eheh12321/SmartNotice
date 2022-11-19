package sejong.smartnotice.helper.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.announce.AnnounceStatus;
import sejong.smartnotice.service.AnnounceService;
import sejong.smartnotice.service.FileService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileSaveListener {

    private final AnnounceService announceService;
    private final FileService fileService;

    @Async
    @EventListener
    public void saveFile(CreatedAnnounceEvent event) throws InterruptedException {
        Announce announce = event.getAnnounce();
        log.info(">> 방송 생성 Event 수신 - {}, {}", announce.getTitle(), announce.getType());

        String datePath = null, fileName = null;
        AnnounceStatus status = AnnounceStatus.READY;

        Thread.sleep(5000); // Async 메서드 테스트용 :D

        switch (announce.getType()) {
            case TEXT:
                // **** TTS 종료로 인해 문자방송은 음성 파일 저장하지 않음 ****
                status = AnnounceStatus.DISABLE;
                break;
            case VOICE:
                datePath = fileService.getDirectory(); // 폴더 생성
                fileName = UUID.randomUUID().toString();
                boolean saveResult = fileService.saveFile(event.getAudioContents(), fileName, datePath);
                if(!saveResult) {
                    status = AnnounceStatus.ERROR; // 파일 저장 실패 시
                } else {
                    status = AnnounceStatus.ABLE; // 파일 저장 성공 시
                }
                break;
        }
        announceService.setAnnounceStatus(announce.getId(), "storage" + datePath, fileName, status);
        log.info("<< 방송 저장 완료");
    }
}
