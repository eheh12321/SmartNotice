package sejong.smartnotice.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public abstract class Region {

    @Id @Column(name = "location_id")
    private Long regionCode; // 지역코드

    @Column(name = "region_parent")
    private String parentRegion; // 시, 도

    @Column(name = "region_child")
    private String childRegion; // 시, 군, 구
}
