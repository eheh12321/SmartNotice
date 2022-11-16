package sejong.smartnotice.helper.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.service.FileService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileSaveListener {

    private final FileService fileService;

    // 트랜잭션 커밋 전에 이벤트 실행 (= 영속성 지속 상태)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveFile(CreatedAnnounceEvent event) {
        Announce announce = event.getAnnounce();
        log.info(">> 방송 생성 Event 수신 - {}, {}", announce.getTitle(), announce.getType());

        String datePath = fileService.getDirectory(); // 폴더 생성
        String fileName = UUID.randomUUID().toString();
        fileService.saveFile(event.getAudioContents(), fileName, datePath);
        announce.setAnnounceFileSaved("storage" + datePath, fileName);

        log.info("<< 방송 저장 완료 - {}", fileName);
    }
}
