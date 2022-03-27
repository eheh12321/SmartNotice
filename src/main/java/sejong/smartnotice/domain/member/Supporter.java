package sejong.smartnotice.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.smartnotice.domain.EmergencyAlert;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Data
@Slf4j
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supporter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supporter_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String loginId;
    private String loginPw;
    private String name;
    private String tel;

    public void emergencyCall(EmergencyAlert alert) {
        log.warn("!!!!!!!!!!!!!!긴급 호출이 발생했습니다!!!!!!!!!!!!!");
        log.warn("호출시각: {}", alert.getAlertTime());
        log.warn("받은사람: {}", this.name);
        log.warn("================================================");
    }
}
