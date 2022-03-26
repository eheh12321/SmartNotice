package sejong.smartnotice.domain.member;

import lombok.Data;
import sejong.smartnotice.domain.Device;
import sejong.smartnotice.domain.Town;

import javax.persistence.*;

@Data
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "town_id")
    private Town town;

    private String loginId;
    private String loginPw;
    private String name;
    private String address;
    private String tel;
}
