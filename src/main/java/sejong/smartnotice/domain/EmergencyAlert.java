package sejong.smartnotice.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class EmergencyAlert {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    private LocalDateTime alertTime;
}
