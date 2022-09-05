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
    private double temp; // 온도

    private double rainAmount; // 강수량

    private double humid; // 습도

    private String lastUpdateTime; // 마지막 갱신 시각 (시간 단위)
}
