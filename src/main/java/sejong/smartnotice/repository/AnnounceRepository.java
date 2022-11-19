package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.announce.Announce;

import java.util.List;

public interface AnnounceRepository extends JpaRepository<Announce, Long> {

    @Query("select distinct a from Announce a join fetch a.townAnnounceList at join fetch at.town")
    List<Announce> findAllAnnounceWithTown(); // 방송과 관련된 마을을 한꺼번에 전부 조인해서 가져온다

    @Query("select distinct a from Announce a join fetch a.townAnnounceList at join fetch at.town where at.town in(?1)")
    List<Announce> findAnnouncesByTownList(List<Town> townList); // 마을 목록에 해당하는 방송을 전부 가져온다
}
