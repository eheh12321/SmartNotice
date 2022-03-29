package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.device.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
}
