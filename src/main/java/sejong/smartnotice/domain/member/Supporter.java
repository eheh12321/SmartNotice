package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.*;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Supporter extends UserAccount {

    @ManyToOne(fetch = LAZY)
    @JoinColumn
    private User user;

    public static Supporter createSupporter(String name, String tel, Account account) {
        Supporter supporter = new Supporter();
        supporter.createUserAccount(name, tel, account);
        return supporter;
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

    public void modifySupporterInfo(String name, String tel) {
        super.changeUserAccountInfo(name, tel);
    }

    @Override // 사용자 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority("ROLE_SUPPORTER"));
        return roles;
    }
}
