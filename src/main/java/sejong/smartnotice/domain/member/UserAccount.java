package sejong.smartnotice.domain.member;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sejong.smartnotice.domain.AuditingFields;

import javax.persistence.*;
import java.util.Collection;

@Getter
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class UserAccount extends AuditingFields implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_account_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String tel;

    @Embedded
    Account account;

    /**
     * 회원 관련 메서드
     * - protected로 설정해놓고 하위 구현 클래스에서 호출해서 사용하도록 세팅
     */
    // 회원 생성
    protected void createUserAccount(String name, String tel, Account account) {
        this.name = name;
        this.tel = tel;
        this.account = account;
    }

    // 회원 정보 수정
    protected void changeUserAccountInfo(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }

    /**
     * 스프링 시큐리티 세팅 메서드
     * - 상속 하위 클래스가 경우에 맞게 오버라이딩 해서 사용
     */
    @Override // 사용자 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override // 비밀번호 주세요
    public String getPassword() {
        return this.account.getLoginPw();
    }

    @Override // 사용자 유니크 아이디 반환
    public String getUsername() {
        return this.account.getLoginId();
    }

    @Override // 계정 만료 여부 반환
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // 계정 잠금 여부 반환
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 크리덴셜 만료 여부 반환
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override // 계정 사용가능 여부 반환
    public boolean isEnabled() {
        return true;
    }
}
