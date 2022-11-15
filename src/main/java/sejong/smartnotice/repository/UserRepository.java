package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.member.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.name like %?1%")
    List<User> findByNameContaining(String name);

    @Query("select u from User u join fetch u.town")
    List<User> findAllWithTown();

    Optional<User> findByAccount_LoginId(String loginId);

    Optional<User> findByTel(String tel);

    boolean existsByTelOrAccountLoginId(String tel, String loginId);
}
