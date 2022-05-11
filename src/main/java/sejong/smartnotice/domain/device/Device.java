package sejong.smartnotice.domain.device;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import sejong.smartnotice.domain.member.User;

import javax.persistence.*;

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
    private boolean error;

    @ColumnDefault("false")
    private boolean emergency;

    @OneToOne(mappedBy = "device", fetch = LAZY)
    private Sensor sensor;

    private String mac;

    // 단말기 연결상태 점검
    public Long checkError() {
        return this.getId();
    }

    // 단말기 긴급유무
    public Long doAlert() {
        this.error = true;
        return this.getId();
    }

    // 단말기 사용자 변경
    public Long changeUser(User user) {
        this.user = user;
        return user.getId();
    }
}
