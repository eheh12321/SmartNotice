package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.member.Supporter;

import java.util.List;

@Repository
public interface SupporterRepository extends JpaRepository<Supporter, Long> {

    Supporter findByAccountLoginId(String loginId);

    Supporter findByTel(String tel);

    @Query("select s from Supporter s join fetch s.user")
    List<Supporter> findAllWithUser();

    boolean existsSupporterByAccountLoginIdOrTel(String loginId, String tel);
}
