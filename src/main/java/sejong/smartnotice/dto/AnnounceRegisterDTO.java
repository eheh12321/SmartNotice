package sejong.smartnotice.dto;

import lombok.*;
import sejong.smartnotice.domain.announce.AnnounceCategory;
import sejong.smartnotice.domain.announce.AnnounceType;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnounceRegisterDTO {

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
}
