package sejong.smartnotice.domain.device;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.member.User;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id", nullable = false)
    private Long id;

    @OneToOne(mappedBy = "device", fetch = LAZY)
    private User user;

    @ColumnDefault("false")
    private boolean error_sensor; // 센서 장애

    @ColumnDefault("false")
    private boolean error_mqtt; // 통신 장애

    @ColumnDefault("true")
    private boolean available; // 사용 가능 유무

    private String mac;

    // 단말기 연결상태 점검
    public Long checkError() {
        return this.getId();
    }

    // 단말기 사용자 변경
    public Long changeUser(User user) {
        this.user = user;
        return user.getId();
    }
}
