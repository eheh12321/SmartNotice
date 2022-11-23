package sejong.smartnotice.helper.dto.response;

import lombok.Builder;
import lombok.Getter;
import sejong.smartnotice.domain.member.Admin;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AdminResponse {
    private final Long id;
    private final String name;
    private final String tel;
    private final String type;
    private final List<TownResponse> manageTownList;

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
