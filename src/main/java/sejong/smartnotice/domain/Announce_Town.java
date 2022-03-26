package sejong.smartnotice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announce_Town {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announce_town_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "town_id")
    private Town town;

    @ManyToOne
    @JoinColumn(name = "announce_id")
    private Announce announce;
}
