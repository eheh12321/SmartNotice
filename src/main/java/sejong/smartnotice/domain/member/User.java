package sejong.smartnotice.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sejong.smartnotice.domain.Device;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.dto.UserDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // Device의 생명주기가 User에 의해 정해진다
    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "town_id")
    private Town town;

    @OneToMany(mappedBy = "user")
    private List<Supporter> supporterList = new ArrayList<>();

    private String loginId;
    private String loginPw;
    private String name;
    private String address;
    private String tel;
}
