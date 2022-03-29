package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.EmergencyAlert;

@Repository
public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, Long> {
}
