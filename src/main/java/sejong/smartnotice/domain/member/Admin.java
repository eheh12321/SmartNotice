package sejong.smartnotice.domain.member;

import lombok.*;
import sejong.smartnotice.domain.Admin_Town;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.CascadeType.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin")
public class Admin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String tel;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String loginPw;

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

    public void modifyAdminInfo(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return Objects.equals(getId(), admin.getId()) && Objects.equals(getLoginId(), admin.getLoginId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
