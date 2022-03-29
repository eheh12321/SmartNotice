package sejong.smartnotice.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sejong.smartnotice.domain.Admin_Town;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Data
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
    private AdminRole type; // 관리자 타입
    
    private String loginId;
    private String loginPw;
    private String name;
    private String tel;

    public void 로그인() {

    }
    public void 로그아웃() {

    }


}
