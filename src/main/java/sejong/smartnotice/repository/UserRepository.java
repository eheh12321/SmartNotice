package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.member.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
