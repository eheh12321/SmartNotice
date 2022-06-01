package sejong.smartnotice.domain.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sensor_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    private Double temp;
    private Double action;
    private Double lumnc;
    private Double oxy;
    private Double co2;

    private LocalDateTime measureTime;

}
