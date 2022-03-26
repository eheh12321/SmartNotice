package sejong.smartnotice.domain.member;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Supporter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supporter_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String loginId;
    private String loginPw;
    private String name;
    private String address;
}
