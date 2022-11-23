package sejong.smartnotice.helper.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.helper.dto.request.register.UserAccountRegisterDTO;
import sejong.smartnotice.helper.validator.Phone;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class AdminRequest {

    @Getter @Setter
    public static class AdminRegisterRequest extends UserAccountRegisterDTO {
        private AdminType type;

        public AdminRegisterRequest() {}

        public AdminRegisterRequest(String name, String tel, String loginId, String loginPw, AdminType type) {
            super(name, tel, loginId, loginPw);
            this.type = type;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminModifyRequest {

        @NotBlank
        private String name;

        @Phone
        private String tel;

        private List<Long> townIdList;
    }
}
