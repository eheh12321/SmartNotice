package sejong.smartnotice.dto.request;

import lombok.*;
import sejong.smartnotice.domain.member.AdminType;

@Getter @Setter
public class AdminRegisterDTO extends UserAccountRegisterDTO {

    private AdminType type;

    public AdminRegisterDTO() {
    }

    public AdminRegisterDTO(String name, String tel, String loginId, String loginPw, AdminType type) {
        super(name, tel, loginId, loginPw);
        this.type = type;
    }
}
