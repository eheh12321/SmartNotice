package sejong.smartnotice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sejong.smartnotice.domain.member.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyAlert {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime alertTime;
}
