package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Entity
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(insertable = false, updatable = false)
    private String dtype;

    private String name;

    private String tel;

    private String loginId;

    private String loginPw;

    // 회원 정보 수정
    public void changeMemberInfo(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", dtype='" + dtype + '\'' +
                ", name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                '}';
    }
}
