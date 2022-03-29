package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.announce.Announce;

@Repository
public interface AnnounceRepository extends JpaRepository<Announce, Long> {
}
