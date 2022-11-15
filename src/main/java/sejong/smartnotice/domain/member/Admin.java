package sejong.smartnotice.domain.member;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import sejong.smartnotice.domain.Admin_Town;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.CascadeType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Admin extends UserAccount {

    // Admin 커밋 시 자동으로 딸려감
    @OneToMany(mappedBy = "admin", cascade = ALL)
    private final List<Admin_Town> atList = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private AdminType type; // 관리자 타입

    private Admin(AdminType type) {
        this.type = type;
    }

    // 관리자 생성
    public static Admin createAdmin(String name, String tel, Account account, AdminType adminType) {
        Admin admin = new Admin(adminType);
        admin.createUserAccount(name, tel, account);
        return admin;
    }

    // 관리자 정보 수정
    public void modifyAdminInfo(String name, String tel) {
        super.changeUserAccountInfo(name, tel);
    }

    @Override // 사용자 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        if(type.equals(AdminType.SUPER)) {
            roles.add(new SimpleGrantedAuthority("ROLE_SUPER"));
        }
        roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return roles;
    }

}
