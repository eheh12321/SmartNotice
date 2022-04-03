package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.experimental.SuperBuilder;
import sejong.smartnotice.domain.Admin_Town;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin")
public class Admin extends Member {

    // Admin 커밋 시 자동으로 딸려감
    @OneToMany(mappedBy = "admin", cascade = ALL)
    private List<Admin_Town> townList = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private AdminRole role; // 관리자 타입

    // 관리자 생성
    public static Admin createAdmin(String name, String tel, String loginId, String loginPw, AdminRole role) {
        return Admin.builder()
                .name(name)
                .tel(tel)
                .loginId(loginId)
                .loginPw(loginPw)
                .role(role)
                .townList(new ArrayList<>())
                .build();
    }
}
