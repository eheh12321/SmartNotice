package sejong.smartnotice.dto;

import lombok.*;
import sejong.smartnotice.domain.announce.AnnounceCategory;
import sejong.smartnotice.domain.announce.AnnounceType;

import javax.validation.constraints.NotBlank;
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
    private String data; // mp3 stream

    private String contents; // 문자방송 전용
}
