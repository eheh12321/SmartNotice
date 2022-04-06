package sejong.smartnotice.domain.member;

import lombok.*;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Town;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.*;

import static javax.persistence.CascadeType.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin")
public class Admin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String tel;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String loginId;

    @NotBlank
    @Column(nullable = false)
    private String loginPw;

    // Admin 커밋 시 자동으로 딸려감
    @OneToMany(mappedBy = "admin", cascade = ALL)
    private List<Admin_Town> atList = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private AdminType role; // 관리자 타입

    // 관리자 생성
    public static Admin createAdmin(String name, String tel, String loginId, String loginPw, AdminType role) {
        return Admin.builder()
                .name(name)
                .tel(tel)
                .loginId(loginId)
                .loginPw(loginPw)
                .role(role)
                .atList(new ArrayList<>())
                .build();
    }

    public void modifyAdminInfo(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }

    // 관리자 마을 목록 반환 (Admin_Town 엔티티 다루기 복잡하니까 변하게 사용하도록)
    public List<Town> getTownList() {
        List<Town> townList = new ArrayList<>();
        for (Admin_Town at : atList) {
            townList.add(at.getTown());
        }
        return townList;
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
