package sejong.smartnotice.domain.member;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Entity
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(insertable = false, updatable = false)
    private String dtype; // 조회용 (수정불가)

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
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
