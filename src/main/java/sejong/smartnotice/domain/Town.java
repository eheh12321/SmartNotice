package sejong.smartnotice.domain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javax.persistence.CascadeType.ALL;

@Slf4j
@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
    private List<User> userList; // 소속 마을 주민 목록

    @OneToMany(mappedBy = "town", cascade = ALL)
    private List<Announce_Town> announceList;

    @OneToMany(mappedBy = "town", cascade = ALL)
    private List<Admin_Town> atList;

    // 마을 생성
    public static Town createTown(String name, Region region) {
        return Town.builder()
                .name(name)
                .region(region)
                .userList(new ArrayList<>())
                .announceList(new ArrayList<>())
                .atList(new ArrayList<>()).build();
    }

    // 마을 정보 수정
    public void modifyTownInfo(String name, Region region) {
        this.name = name;
        this.region = region;
    }

    @Override
    public String toString() {
        return "Town{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", region=" + region +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Town town = (Town) o;
        return Objects.equals(getId(), town.getId()) && Objects.equals(getName(), town.getName()) && Objects.equals(getRegion(), town.getRegion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getRegion());
    }
}
