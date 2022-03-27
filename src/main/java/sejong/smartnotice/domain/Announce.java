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

}