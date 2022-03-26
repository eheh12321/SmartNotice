package sejong.smartnotice.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Town;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @OneToMany(mappedBy = "admin")
    private List<Admin_Town> townList = new ArrayList<>();

    private String loginId;
    private String loginPw;
    private String name;
    private String tel;
    private String type; // 마을 관리자(마을 관제) / 최고 관리자(Root, 전체 관제)


}
