package sejong.smartnotice.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@ToString
@Getter @Setter
@RedisHash
public class TownData implements Serializable {

    private static final long SerialVersionUID = 1L;

    @Id private Long townId;
    private String townName;

    private Long regionId;

    private String mainAdminName = "-"; // 대표 관리자 이름
    private String mainAdminTel = "-"; // 대표 관리자 연락처

    private int userCnt; // 마을 주민 수
    private int deviceErrorCnt; // 단말기 미연결 주민 수
    private int connectionErrorCnt; // 연걸 장애 주민 수
    private int sensorErrorCnt; // 센서 장애 주민 수

    private int townAdminCnt; // 마을 관리자 수

    private int alertCnt; // 긴급 호출 수
    private int fireAlertCnt; // 화재 알림 수
    private int userAlertCnt; // 사용자 알림 수
    private int motionAlertCnt; // 동작 감지 수

    private int announceCnt; // 마을 방송 수

    private boolean notConfirmedAlert;

    public TownData(Long townId, String townName, Long regionId) {
        this.townId = townId;
        this.townName = townName;
        this.regionId = regionId;
    }
}
