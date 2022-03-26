package sejong.smartnotice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
    private String loginId;
    private String loginPw;
}
