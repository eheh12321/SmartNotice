package sejong.smartnotice.domain.announce;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import sejong.smartnotice.domain.Announce_Town;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;

import javax.persistence.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "announce")
public class Announce {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announce_id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "announce", cascade = CascadeType.ALL)
    private List<Announce_Town> atList = new ArrayList<>();
    
    @Column(nullable = false)
    private String producer; // 방송한 사람

    @Column(nullable = false)
    private LocalDateTime time; // 방송시각

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnounceCategory category; // 일반 or 재난

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnounceType type; // 음성 or 문자

    private String directory; // 파일 저장위치
    
    private String fileName; // 파일 이름

    @Column(nullable = false)
    private String title; // 방송 제목

    @Column(nullable = false)
    private String contents; // 문자방송 내용
    
    // 파일 저장 경로 리턴
    public String getFullPath() {
        return File.separator + directory + File.separator + fileName + ".mp3";
    }

    public static Announce makeAnnounce(String producer, String contents, AnnounceCategory category, AnnounceType type,
                                        List<Town> townList, String directory, String fileName, String title) {
        // 1. 방송 생성
        Announce announce = Announce.builder()
                .producer(producer)
                .contents(contents)
                .title(title)
                .category(category)
                .type(type)
                .atList(new ArrayList<>())
                .time(LocalDateTime.now())
                .directory(directory)
                .fileName(fileName).build();

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
