package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;

import java.util.List;
import java.util.Optional;

@Repository
public interface TownRepository extends JpaRepository<Town, Long> {

    List<Town> findByNameContaining(@Param("name") String name);

    boolean existsByRegionAndName(Region region, String name);

    @Query("select t from Town t join fetch t.region")
    List<Town> findAllWithRegion();

    @Query("select t from Town t join fetch t.atList at where at.admin.id=?1")
    List<Town> findTownsByAdmin(Long adminId);

    @Query("select t from Town t join fetch t.region where t.id=?1")
    List<Town> findByIdWithRegion(Long townId);
}
