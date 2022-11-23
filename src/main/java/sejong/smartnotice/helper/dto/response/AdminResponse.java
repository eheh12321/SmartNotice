package sejong.smartnotice.helper.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.smartnotice.domain.member.Admin;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {
    private Long id;
    private String name;
    private String tel;
    private String type;
    private List<TownResponse> manageTownList;

    public static AdminResponse from(Admin admin) {
        List<TownResponse> manageTownList = admin.getTownAdminList().stream()
                .map(townAdmin -> TownResponse.from(townAdmin.getTown()))
                .collect(Collectors.toList());

        return AdminResponse.builder()
                .id(admin.getId())
                .name(admin.getName())
                .tel(admin.getTel())
                .type(admin.getType().name())
                .manageTownList(manageTownList).build();
    }
}
