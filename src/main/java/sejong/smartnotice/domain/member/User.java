package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.Town;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Slf4j
@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String tel;

    @Embedded
    Account account;

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
    private List<Supporter> supporterList = new ArrayList<>();

    public static User createUser(String name, String tel, String address, String birth, Town town, Account account) {
        return User.builder()
                .name(name)
                .tel(tel)
                .address(address)
                .birth(birth)
                .account(account)
                .supporterList(new ArrayList<>())
                .alertList(new ArrayList<>())
                .town(town).build();
    }

    public void modifyUserInfo(String name, String tel, String address, String info, String birth) {
        this.name = name;
        this.tel = tel;
        this.address = address;
        this.info = info;
        this.birth = birth;
    }

    public void modifyDevice(Device device) {
        this.device = device;
        device.changeUser(this);
    }

    public void modifyTown(Town town) {
        this.town.getUserList().remove(this); // 기존 마을 정보 삭제
        this.town = town;
        town.getUserList().add(this);
    }

    // 마을 주민 계정을 관리자 등록
    public void modifyUserIsAdmin() {
        this.isAdmin = true;
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

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", address='" + address + '\'' +
                ", info='" + info + '\'' +
                ", birth=" + birth +
                '}';
    }

    /////////// 스프링 시큐리티 설정

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        return roles;
    }

    @Override // 비밀번호 주세요
    public String getPassword() {
        return this.account.getLoginPw();
    }

    @Override // 사용자 유니크 아이디 반환
    public String getUsername() {
        return this.account.getLoginId();
    }

    @Override // 계정 만료 여부 반환
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // 계정 잠금 여부 반환
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 크리덴셜 만료 여부 반환
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override // 계정 사용가능 여부 반환
    public boolean isEnabled() {
        return true;
    }
}
