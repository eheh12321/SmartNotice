package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sejong.smartnotice.domain.EmergencyAlert;

import javax.persistence.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.*;

@Getter
@Slf4j
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Supporter implements UserDetails {

    @Id @GeneratedValue
    @Column(name = "supporter_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String tel;

    private String name;

    @Embedded
    Account account;

    public static Supporter createSupporter(String name, String tel, Account account) {
        return Supporter.builder()
                .name(name)
                .tel(tel)
                .account(account).build();
    }

    // 보호자와 마을주민 연결
    public void connectUser(User user) {
        log.info("마을 주민: {}", user.getName());
        log.info("보호자: {}", this.getName());
        this.user = user; // 양방향 연결
        if(!user.getSupporterList().contains(this)) {
            user.getSupporterList().add(this);
        }
    }

    // 보호자 정보 수정
    public void modifySupporterInfo(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }

    @Override
    public String toString() {
        return this.name;
    }

    ///////////// 스프링 시큐리티

    @Override // 사용자 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority("ROLE_SUPPORTER"));
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
