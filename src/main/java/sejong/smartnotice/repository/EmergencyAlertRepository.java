package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.EmergencyAlert;

import java.util.List;

@Repository
public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, Long> {

    @Query("select ea from EmergencyAlert ea join fetch ea.user where ea.user.id=?1")
    List<EmergencyAlert> findWithUserByUserId(Long userId);
}
