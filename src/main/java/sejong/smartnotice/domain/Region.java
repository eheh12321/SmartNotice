package sejong.smartnotice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Region {

    @Id @Column(name = "location_id")
    private Long regionCode; // 지역코드

    @Column(name = "main_region")
    private String mainRegion; // 시, 도

    @Column(name = "sub_region")
    private String subRegion; // 시, 군, 구

    @Override
    public String toString() {
        return mainRegion + " " + subRegion;
    }
}
