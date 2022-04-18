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

    @NotBlank
    private String text;

    @NotNull
    private AnnounceCategory category;

    @NotNull
    private AnnounceType type;

    @NotNull
    private List<Long> townId;

    private boolean created;

    private String path;

    private String fileName;
}
