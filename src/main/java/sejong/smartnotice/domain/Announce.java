package sejong.smartnotice.domain;

import lombok.*;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announce {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announce_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @OneToMany(mappedBy = "announce", cascade = CascadeType.ALL)
    private List<Announce_Town> atList = new ArrayList<>();

    private String title; // 방송제목
    private LocalDateTime time; // 방송시각
    private String category; // 일반 or 재난
    private String type; // 음성 or 문자
    private String store; // 파일 저장위치

    public static Announce makeAnnounce(Admin admin, String title, String category, String type, List<Town> townList) {
        // 1. 방송 생성
        Announce announce = Announce.builder()
                .admin(admin)
                .title(title)
                .category(category)
                .type(type)
                .atList(new ArrayList<>())
                .time(LocalDateTime.now())
                .store("./저장소경로").build();

        // 2. 마을에 방송 전파
        for (Town town : townList) {
            Announce_Town at = Announce_Town.builder()
                    .announce(announce)
                    .town(town).build();
            announce.atList.add(at);
            at.setTown(town); // 양방향 맺어주기

            // 2-1. 마을에 속한 주민에게 방송 전파
            List<User> userList = town.getUserList();
            for (User user : userList) {
                user.receiveAnnounce(announce);
            }
        }
        return announce;
    }
}
