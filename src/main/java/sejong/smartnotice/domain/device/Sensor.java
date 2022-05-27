package sejong.smartnotice.domain.device;

import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
@Getter
@Entity
public class Sensor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sensor_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    private Double temp;
    private Double motion;
    private Double lumi;
    private Double oxygen;
    private Double co2;

    private LocalDateTime receiveTime;

    @Override
    public String toString() {
        return "Sensor{" +
                "id=" + id +
                ", temp=" + temp +
                ", motion=" + motion +
                ", lumi=" + lumi +
                ", oxygen=" + oxygen +
                ", co2=" + co2 +
                ", receiveTime=" + receiveTime +
                '}';
    }
}
