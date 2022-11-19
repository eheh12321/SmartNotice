package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;

import java.util.List;
import java.util.Set;

public interface TownRepository extends JpaRepository<Town, Long> {

    List<Town> findByNameContaining(@Param("name") String name);

    boolean existsByRegionAndName(Region region, String name);

    @Query("select t from Town t join fetch t.region")
    List<Town> findAllWithRegion();

    @Query("select t from Town t join fetch t.townAdminList at where at.admin.id=?1")
    List<Town> findTownsByAdmin(Long adminId);

    @Query("select t from Town t join fetch t.region where t.id=?1")
    List<Town> findByIdWithRegion(Long townId);

    @Query("select t from Town t where t.id in ?1")
    Set<Town> findTownsByTownIdSet(Set<Long> townIdSet); // IN 문을 이용해 한꺼번에 조회하는 용도 (Set)

    @Query("select t from Town t where t.id in ?1")
    List<Town> findTownsByTownIdList(List<Long> townIdList); // IN 문을 이용해 한꺼번에 조회하는 용도 (List)
}
