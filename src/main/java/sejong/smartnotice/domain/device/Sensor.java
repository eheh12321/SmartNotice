package sejong.smartnotice.domain.device;

import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Entity
public abstract class Sensor {

    @Id
    @Column(name = "device_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ColumnDefault("0")
    private Double sensor1;
    @ColumnDefault("0")
    private Double sensor2;
    @ColumnDefault("0")
    private Double sensor3;
    @ColumnDefault("0")
    private Double sensor4;
    @ColumnDefault("0")
    private Double sensor5;

    private LocalDateTime receiveTime;

    public Map<String, Double> getRecentData() {
        Map<String, Double> dataMap = new HashMap<>();
        dataMap.put("센서1", sensor1);
        dataMap.put("센서2", sensor2);
        dataMap.put("센서3", sensor3);
        dataMap.put("센서4", sensor4);
        dataMap.put("센서5", sensor5);
        return dataMap;
    }
}
