package sejong.smartnotice.helper.dto.request;

import lombok.*;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.announce.AnnounceCategory;
import sejong.smartnotice.domain.announce.AnnounceStatus;
import sejong.smartnotice.domain.announce.AnnounceType;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnounceRequest {

    @NotNull
    private Long adminId;

    @NotNull
    private AnnounceCategory category;

    @NotNull
    private AnnounceType type;

    @NotNull
    private List<Long> townId;

    @NotNull
    private String title; // 방송 제목

    private String textData; // 방송 텍스트 본문

    private String voiceData; // 음성 파일

    public Announce toEntity(String producer) {
        return Announce.builder()
                .producer(producer)
                .contents(textData)
                .title(title)
                .category(category)
                .type(type)
                .status(AnnounceStatus.READY)
                .townAnnounceList(new ArrayList<>())
                .time(LocalDateTime.now()).build();
    }
}
