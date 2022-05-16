package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.EmergencyAlert;

import java.util.List;

@Repository
public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, Long> {

    @Query("select em from EmergencyAlert em join fetch em.user u")
    public List<EmergencyAlert> findAllWithUser();

    @Query("select em from EmergencyAlert em join fetch em.user u where u.town.id=?1")
    public List<EmergencyAlert> findAllWithUserByTown(Long townId);
}
