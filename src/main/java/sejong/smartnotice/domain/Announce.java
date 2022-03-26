package sejong.smartnotice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sejong.smartnotice.domain.member.Admin;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announce {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announce_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @OneToMany(mappedBy = "announce")
    private List<Announce_Town> towns = new ArrayList<>();

    private String title;
    private LocalDateTime time;
    private String type;
    private String store; // 파일 저장위치
    
    public static Announce createAnnounce(Admin admin, Announce_Town... announce_towns) {
        Announce announce = new Announce();
        announce.setTitle("방송");
        announce.setType("일반");
        announce.setStore("./저장소");
        announce.setTime(LocalDateTime.now());
        for (Announce_Town at : announce_towns) {
            announce.towns.add(at);
        }
        return announce;
    }
}
