package sejong.smartnotice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sejong.smartnotice.domain.member.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Town {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "town_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region; // 소속 지역코드

    @OneToMany(mappedBy = "town")
    private List<User> userList = new ArrayList<>();


}
