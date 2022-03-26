package sejong.smartnotice.domain.member;

import lombok.Data;
import sejong.smartnotice.domain.Announce;

import javax.persistence.*;

@Data
@Entity
public class Admin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    private String loginId;
    private String loginPw;
    private String name;
    private String tel;
    private String type;
}
