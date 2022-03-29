package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import sejong.smartnotice.domain.EmergencyAlert;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Getter
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

    public static Supporter createSupporter(String name, String tel, String loginId, String loginPw) {
        return Supporter.builder()
                .name(name)
                .tel(tel)
                .loginId(loginId)
                .loginPw(loginPw).build();
    }

    // 보호자와 마을주민 연결
    public void connectUser(User user) {
        log.info("== 보호자와 마을주민 연결 ==");
        this.user = user;
        if(!user.getSupporterList().contains(this)) {
            user.getSupporterList().add(this);
        }
    }

    public void changeInfo(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }

    // 서비스 계층에서 검증이후 들어와야함
    public void changePassword(String inputId, String inputPw) {
        this.loginId = inputId;
        this.loginPw = inputPw;
    }

    // 테스트용(임베디드 단에서 구현할것)
    public void emergencyCall(EmergencyAlert alert) {
        log.warn("!!!!!!!!!!!!!!긴급 호출이 발생했습니다!!!!!!!!!!!!!");
        log.warn("호출시각: {}", alert.getAlertTime());
        log.warn("받은사람: {}", this.name);
        log.warn("================================================");
    }
}
