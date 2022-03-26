package sejong.smartnotice.domain;

import lombok.Data;
import sejong.smartnotice.domain.member.Admin;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Announce {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announce_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    private String title;
    private LocalDateTime time;
    private String type;
    private String store; // 파일 저장위치
}
