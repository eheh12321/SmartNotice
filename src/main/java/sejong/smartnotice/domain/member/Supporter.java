package sejong.smartnotice.domain.member;

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
    private String tel;
}
