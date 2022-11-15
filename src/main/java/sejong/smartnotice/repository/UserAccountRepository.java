package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.smartnotice.domain.member.UserAccount;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByAccount_LoginId(String loginId);

    Optional<UserAccount> findByTel(String tel);
}
