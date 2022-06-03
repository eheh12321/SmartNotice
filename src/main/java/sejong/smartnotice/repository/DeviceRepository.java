package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.device.Device;

import java.util.List;
import java.util.Map;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("select d.id from Device d where d.emergency_fire is true")
    List<Long> findByEmergency_fireIsTrue();
}
