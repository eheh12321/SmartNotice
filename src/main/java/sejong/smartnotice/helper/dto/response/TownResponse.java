package sejong.smartnotice.helper.dto.response;

import lombok.Builder;
import lombok.Getter;
import sejong.smartnotice.domain.Town;

@Getter
@Builder
public class TownResponse {
    private final Long id;
    private final String name;
    private final String parentRegion;
    private final String childRegion;

    public static TownResponse from(Town town) {
        return TownResponse.builder()
                .id(town.getId())
                .name(town.getName())
                .parentRegion(town.getRegion().getParentRegion())
                .childRegion(town.getRegion().getChildRegion()).build();
    }
}
