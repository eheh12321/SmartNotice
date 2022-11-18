package sejong.smartnotice.helper.dto.request;

import lombok.*;
import sejong.smartnotice.helper.validator.Phone;

import javax.validation.constraints.NotBlank;

@Getter
public class AdminModifyRequest {

    @Setter
    private Long id;

    @NotBlank
    private final String name;

    @Phone
    private final String tel;

    public AdminModifyRequest(Long id, String name, String tel) {
        this.id = id;
        this.name = name;
        this.tel = tel;
    }
}
