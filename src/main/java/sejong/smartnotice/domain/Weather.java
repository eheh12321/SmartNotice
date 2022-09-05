package sejong.smartnotice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Weather {

    private Double temp; // 온도

    private Double rainAmount; // 강수량

    private Double humid; // 습도

    private String lastUpdateTime; // 마지막 갱신 시각 (시간 단위)
}
