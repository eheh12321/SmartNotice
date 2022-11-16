package sejong.smartnotice.helper.dto.request.register;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class UserRegisterDTO extends UserAccountRegisterDTO {

    @NotBlank
    private String address;

    @NotNull
    private Long townId;

    @NotNull
    private String birth;

    public UserRegisterDTO() {
    }

    public UserRegisterDTO(String name, String tel, String loginId, String loginPw, String address, Long townId, String birth) {
        super(name, tel, loginId, loginPw);
        this.address = address;
        this.townId = townId;
        this.birth = birth;
    }
}
