package sejong.smartnotice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private String name;
    private String address;
    private String tel;
    private String info;
    private int age;
}
