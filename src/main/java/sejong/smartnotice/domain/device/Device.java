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

    @ColumnDefault("false")
    private boolean error_sensor; // 온도 센서 장애

    @ColumnDefault("false")
    private boolean error_temp; // 온도 센서 장애

    @ColumnDefault("false")
    private boolean error_co2; // 온도 센서 장애

    @ColumnDefault("false")
    private boolean error_oxy; // 온도 센서 장애

    @ColumnDefault("false")
    private boolean error_lumnc; // 온도 센서 장애

    @ColumnDefault("false")
    private boolean error_action; // 온도 센서 장애

    @ColumnDefault("false")
    private boolean error_mqtt; // 통신 장애

    @ColumnDefault("true")
    private boolean available; // 사용 가능 유무

    private String mac;
}
