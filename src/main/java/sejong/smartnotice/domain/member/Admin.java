package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Town;

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

//    @Enumerated(value = EnumType.STRING)
//    @ColumnDefault("ADMIN")
//    private AdminRole role; // 관리자 타입

    // 관리자 생성
    public static Admin createAdmin(String name, String tel, String loginId, String loginPw, AdminRole role) {
        return Admin.builder()
                .name(name)
                .tel(tel)
                .loginId(loginId)
                .loginPw(loginPw)
                .townList(new ArrayList<>())
                .build();
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
}
