package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import sejong.smartnotice.domain.EmergencyAlert;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Getter
@Slf4j
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Supporter extends Member {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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
        log.info("유저: {}", user.getName());
        log.info("보호자: {}", this.getName());
        this.user = user;
        if(!user.getSupporterList().contains(this)) {
            user.getSupporterList().add(this);
        }
    }

    // 테스트용(임베디드 단에서 구현할것)
    public void emergencyCall(EmergencyAlert alert) {
        log.warn("!!!!!!!!!!!!!!긴급 호출이 발생했습니다!!!!!!!!!!!!!");
        log.warn("호출시각: {}", alert.getAlertTime());
        log.warn("받은사람: {}", this.getName());
        log.warn("================================================");
    }
}
