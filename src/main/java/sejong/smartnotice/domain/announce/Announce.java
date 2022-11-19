package sejong.smartnotice.domain.announce;

import lombok.*;
import sejong.smartnotice.domain.TownAnnounce;

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
    private List<TownAnnounce> townAnnounceList = new ArrayList<>();

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnounceStatus status;

    private String directory; // 파일 저장위치
    
    private String fileName; // 파일 이름

    @Column(nullable = false)
    private String title; // 방송 제목

    @Column(nullable = false)
    private String contents; // 문자방송 내용
    
    // 파일 저장 경로 리턴
    public String getFullPath() {
        return File.separator + directory + fileName + ".mp3";
    }

    // 파일 저장 성공 시 파일 경로 및 이름 세팅
    public void setAnnounceFileSaved(String directory, String fileName, AnnounceStatus status) {
        this.directory = directory;
        this.fileName = fileName;
        this.status = status;
    }
}
