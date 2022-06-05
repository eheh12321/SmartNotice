package sejong.smartnotice.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
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
    @Column(name = "alert_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime alertTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType;

    @ColumnDefault("0") // == false
    private boolean confirmed; // 관리자 확인 유무

    public static EmergencyAlert createAlert(User user, AlertType alertType) {
        EmergencyAlert alert = EmergencyAlert.builder()
                .user(user)
                .alertType(alertType)
                .alertTime(LocalDateTime.now()).build();
        return alert;
    }

    public void alertConfirm() {
        this.confirmed = true;
    }
}
