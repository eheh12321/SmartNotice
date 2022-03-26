package sejong.smartnotice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Device {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    private Double sensor1;
    private Double sensor2;
    private boolean error;
    private boolean emergency;
}
