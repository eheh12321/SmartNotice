package sejong.smartnotice.domain.member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Account {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String loginId;

    @NotBlank
    @Column(nullable = false)
    private String loginPw;

    // private int failCount; --> 로그인 5회 이상 실패 시 1분간 로그인 불가
    // private LocalDateTime lastLoginTime; --> 마지막 로그인 이력

    // 추가적인 메소드가 들어갈 수 있다
    public void changePassword(String newEncodedPassword) {
        this.loginPw = newEncodedPassword;
    }

    public static Account createAccount(String id, String password, PasswordEncoder passwordEncoder) {
        String encodedPassword = passwordEncoder.encode(password);
        return new Account(id, encodedPassword);
    }
}
