package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.Town;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Slf4j
@Getter
@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String tel;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String loginPw;

    @Column(length = 50)
    private String address;

    private String info;

    private int age;

    // Device의 생명주기가 User에 의해 정해진다
    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "town_id")
    private Town town;

    @OneToMany(mappedBy = "user")
    private List<Supporter> supporterList = new ArrayList<>();

    public static User createUser(String name, String tel, String address, int age, Town town, String id, String pw) {
        return User.builder()
                .name(name)
                .tel(tel)
                .address(address)
                .age(age)
                .loginId(id)
                .loginPw(pw)
                .supporterList(new ArrayList<>())
                .town(town).build();
    }

    public void changeUserInfo(String name, String tel, String address, String info, int age) {
        this.name = name;
        this.tel = tel;
        this.address = address;
        this.info = info;
        this.age = age;
    }

    public void changeDevice(Device device) {
        this.device = device;
        device.changeUser(this);
    }

    public void changeTown(Town town) {
        this.town.getUserList().remove(this); // 기존 마을 정보 삭제
        this.town = town;
        town.getUserList().add(this);
    }

    /// 방송 수신 테스트용 (실제로는 임베디드 단에서 구현) ///
    public void receiveAnnounce(Announce announce) {
        log.warn("=======방송이 도착했습니다=========");
        log.warn("방송명: {}", announce.getTitle());
        log.warn("방송타입: {}", announce.getType());
        log.warn("방송을 받은 사람: {}", this.getName());
        log.warn("===============================");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
