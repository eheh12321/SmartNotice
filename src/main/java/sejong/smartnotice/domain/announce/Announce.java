package sejong.smartnotice.domain.announce;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import sejong.smartnotice.domain.Announce_Town;
import sejong.smartnotice.domain.Town;
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
    @Column(name = "announce_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @OneToMany(mappedBy = "announce", cascade = CascadeType.ALL)
    private List<Announce_Town> atList = new ArrayList<>();

    @Column(nullable = false)
    private String title; // 방송제목

    @Column(nullable = false)
    private LocalDateTime time; // 방송시각

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnounceCategory category; // 일반 or 재난

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnounceType type; // 음성 or 문자

    @ColumnDefault("[저장소 경로]")
    private String store; // 파일 저장위치

    public static Announce makeAnnounce(Admin admin, String title, AnnounceCategory category, AnnounceType type, List<Town> townList) {
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
