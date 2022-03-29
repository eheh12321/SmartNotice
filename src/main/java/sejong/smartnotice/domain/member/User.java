package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import sejong.smartnotice.domain.Announce;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.Town;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Slf4j
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // Device의 생명주기가 User에 의해 정해진다
    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "town_id")
    private Town town;

    @OneToMany(mappedBy = "user")
    private List<Supporter> supporterList = new ArrayList<>();

    private String name;
    private String address;
    private String tel;
    private String loginId;
    private String loginPw;

    public long changeDevice(Device device) {
        this.device = device;
        return device.getId();
    }

    public long changeTown(Town town) {
        this.town.getUserList().remove(this); // 기존 마을 정보 삭제
        this.town = town;
        town.getUserList().add(this);
        return town.getId();
    }

    public void changeCredential(String inputId, String inputPw) {
        this.loginId = inputId;
        this.loginPw = inputPw;
    }

    public void changeUserInfo(String name, String address, String tel) {
        this.name = name;
        this.address = address;
        this.tel = tel;
    }

    /// 방송 수신 테스트용 (실제로는 임베디드 단에서 구현) ///
    public void receiveAnnounce(Announce announce) {
        log.warn("=======방송이 도착했습니다=========");
        log.warn("방송명: {}", announce.getTitle());
        log.warn("방송타입: {}", announce.getType());
        log.warn("방송을 받은 사람: {}", this.name);
        log.warn("===============================");
    }
}
