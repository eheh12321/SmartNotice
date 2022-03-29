package sejong.smartnotice.domain;

import lombok.*;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.FetchType.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyAlert {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    private LocalDateTime alertTime;

    public static EmergencyAlert createAlert(User user) {
        // 마을주민 단말기 참조
        Device device = user.getDevice();
        // 단말기 긴급상황 표시
        device.doAlert();
        // Alert 생성
        EmergencyAlert alert = EmergencyAlert.builder()
                .device(device)
                .alertTime(LocalDateTime.now()).build();

        // 마을 주민과 연결된 보호자에게 전화알림
        List<Supporter> supporterList = user.getSupporterList();
        for (Supporter supporter : supporterList) {
            supporter.emergencyCall(alert); // API 들어가야됨
        }
        return alert;
    }
}
