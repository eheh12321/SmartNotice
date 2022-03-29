package sejong.smartnotice.domain.member;

import lombok.*;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Town;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    // Admin 커밋 시 자동으로 딸려감
    @OneToMany(mappedBy = "admin", cascade = ALL)
    private List<Admin_Town> townList = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private AdminRole role; // 관리자 타입
    
    private String loginId;
    private String loginPw;
    private String name;
    private String tel;

    // 관리자 생성
    public static Admin createAdmin(String name, String tel, String loginId, String loginPw, AdminRole role) {
        return Admin.builder()
                .name(name)
                .tel(tel)
                .loginId(loginId)
                .loginPw(loginPw)
                .townList(new ArrayList<>())
                .role(role).build();
    }

    // 관리자 관리 마을 변경
    public void setManageTown(List<Town> townList) {
        List<Admin_Town> atList = new ArrayList<>();
        for (Town town : townList) {
            Admin_Town at = Admin_Town.builder()
                    .town(town)
                    .admin(this).build();
            atList.add(at);
            at.setAdmin(this); // 양방향 맺어주기
        }
        this.townList = atList; // 배열 갈아끼기
    }
    
    // 서비스 계층에서 검증이후 들어와야함
    public void changePassword(String inputId, String inputPw) {
        this.loginId = inputId;
        this.loginPw = inputPw;
    }

    public void changeAdminInfo(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }
}
