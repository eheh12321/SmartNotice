package sejong.smartnotice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sejong.smartnotice.domain.announce.Announce;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TownAnnounce {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "town_announce_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "town_id", nullable = false)
    private Town town;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announce_id", nullable = false)
    private Announce announce;

    @Override
    public String toString() {
        return town.getName();
    }

    // 연관관계 설정 메서드
    public void createAnnounce() {
        town.getAnnounceList().add(this);
        announce.getTownAnnounceList().add(this);
    }
}
