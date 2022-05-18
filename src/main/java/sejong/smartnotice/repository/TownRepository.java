package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;

import java.util.List;
import java.util.Optional;

@Repository
public interface TownRepository extends JpaRepository<Town, Long> {

    @Query("select t from Town t where t.name like %?1%")
    List<Town> findByNameContaining(String name);

    boolean existsByRegionAndName(Region region, String name);

    @Query("select t from Town t join fetch t.region")
    List<Town> findAllWithRegion();

    @Query("select t from Town t join fetch t.region where t.id=?1")
    Optional<Town> findById(Long townId);
}
