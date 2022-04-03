package sejong.smartnotice.domain;

import lombok.*;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Town {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "town_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region; // 소속 지역코드

    @OneToMany(mappedBy = "town")
    private List<User> userList = new ArrayList<>(); // 소속 마을 주민 목록

    @OneToMany(mappedBy = "town", cascade = CascadeType.ALL)
    private List<Admin_Town> adminList = new ArrayList<>(); // 소속 관리자 목록

    public void changeTown(String name, Region region) {
        this.name = name;
        this.region = region;
    }

    // 마을 관리자 등록
    public void addTownAdmin(Admin admin) {
        Admin_Town at = Admin_Town.builder()
                .admin(admin)
                .town(this).build();
        adminList.add(at);
        at.setTown(this); // 양방향 설정
    }

    @Override
    public String toString() {
        return "Town{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", region=" + region +
                '}';
    }
}
