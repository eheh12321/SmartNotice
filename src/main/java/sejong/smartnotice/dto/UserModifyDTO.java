package sejong.smartnotice.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserModifyDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String tel;

    private String info;

    @NotNull
    @Range(min = 1, max = 150)
    private Integer age;
}
