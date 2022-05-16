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

    public static EmergencyAlert createAlert(User user) {
        EmergencyAlert alert = EmergencyAlert.builder()
                .user(user)
                .alertTime(LocalDateTime.now()).build();
        return alert;
    }
}
