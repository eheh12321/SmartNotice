package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.announce.Announce;

import java.util.List;

@Repository
public interface AnnounceRepository extends JpaRepository<Announce, Long> {

    @Query("select a from Announce a join fetch a.atList at join fetch at.town where at.town.id=?1")
    List<Announce> findAllAnnounceToTown(Long townId);
}
