package sejong.smartnotice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import sejong.smartnotice.domain.member.AdminRole;

@Data
@AllArgsConstructor
public class AdminDTO {
    private String name;
    private String tel;
    private AdminRole type;
}
