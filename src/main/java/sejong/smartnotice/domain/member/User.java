package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.Town;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.FetchType.*;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends UserAccount {

    @Column(length = 50)
    private String address;

    private String info;

    private String birth; // 생년월일

    @ColumnDefault("0")
    private boolean isAdmin; // 마을 주민이 관리자인지?

    // Device의 생명주기가 User에 의해 정해진다
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @OneToMany(mappedBy = "user")
    private List<EmergencyAlert> alertList;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "town_id")
    private Town town;

    @OneToMany(mappedBy = "user")
    private final List<Supporter> supporterList = new ArrayList<>();

    private User(String address, String birth, Town town) {
        this.address = address;
        this.birth = birth;
        this.town = town;
    }

    public static User createUser(String name, String tel, String address, String birth, Town town, Account account) {
        User user = new User(address, birth, town);
        user.createUserAccount(name, tel, account);
        return user;
    }

    public void modifyUserInfo(String name, String tel, String address, String info, String birth) {
        super.changeUserAccountInfo(name, tel);
        this.address = address;
        this.info = info;
        this.birth = birth;
    }

    public void modifyUserDevice(Device device) {
        this.device = device;
    }

    // 마을 주민 계정을 관리자 등록
    public void modifyUserIsAdmin() {
        this.isAdmin = true;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        return roles;
    }

}
