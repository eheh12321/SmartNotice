package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.member.Supporter;

@Repository
public interface SupporterRepository extends JpaRepository<Supporter, Long> {
}
