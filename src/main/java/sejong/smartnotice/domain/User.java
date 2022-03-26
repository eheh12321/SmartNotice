package sejong.smartnotice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String loginId;
    private String loginPw;
    private String name;
    private String address;
    private String tel;
}
