package sejong.smartnotice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sejong.smartnotice.domain.member.Admin;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminListPageDTO {
    private Admin admin;
    private List<Long> adminTownIdList;
}
