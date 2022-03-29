package sejong.smartnotice.domain.device;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Sensor {

    @Id
    @Column(name = "device_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    private Double sensor1;
    private Double sensor2;
    private Double sensor3;
    private Double sensor4;
    private Double sensor5;

    private LocalDateTime receiveTime;
}
