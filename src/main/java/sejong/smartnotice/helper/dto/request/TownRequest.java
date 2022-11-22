package sejong.smartnotice.helper.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TownRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TownCreateRequest {
        @NotBlank
        private String name;

        @NotNull
        private Long regionCode;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TownModifyRequest {

        @NotBlank
        private String name;

        @NotNull
        private Long regionCode;
    }
}
