package sejong.smartnotice.helper.dto;

import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModifyDTO {

    private Long id;

    private String name;

    private String address;

    private String birth;

    private String tel;

    private String info;
}
